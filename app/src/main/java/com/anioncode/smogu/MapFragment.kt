package com.anioncode.smogu

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.Model.ModelAll.FindAll
import com.anioncode.smogu.Model.ModelIndex.ModelIndex
import com.anioncode.smogu.Model.ModelSensor.SensorsName
import com.anioncode.smogu.MyVariables.Companion.modelIndexList
import com.anioncode.smogu.MyVariables.Companion.stationList
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() {

    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
//    lateinit var stationList: List<FindAll>
//    lateinit var sensorsNameList: List<SensorsName>
//    lateinit var modelIndexList: ArrayList<ModelIndex>
    var iterator = 0;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_map, container, false)


        //variable
        val Poland = LatLngBounds(LatLng(47.0, 14.0), LatLng(56.5, 24.3))

        if (modelIndexList.size ==0){
            getDataFromApi()
            setPermission()
            setMapFragment(Poland)

        }else{
            setPermission()
            setMapFragment(Poland)

            Handler().postDelayed(Runnable {
                getAllData();
                RelativeLoader.visibility = View.GONE

            }, 1000)

        }



        view.fab.setOnClickListener(
            View.OnClickListener {
                val location = CameraUpdateFactory.newLatLngBounds(Poland, 0)
                // googleMap.moveCamera(location)
                googleMap.animateCamera(location);
            }
        )

        return view
    }


    private fun setMapFragment(Poland: LatLngBounds) {
        mapFragment = childFragmentManager.findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap.isMyLocationEnabled = true
            try {
                val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireActivity(), R.raw.style_json
                    )
                )
                if (!success) {

                }

            } catch (e: Resources.NotFoundException) {

            }

            //  googleMap.setMaxZoomPreference(17.0f)
            //  googleMap.maxZoomLevel

            googleMap.setMinZoomPreference(5.4f)

            googleMap.setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback {

                // Permission to access the location is missing. Show rationale and request permission
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Poland, 0))
//                Handler().postDelayed(Runnable {
//                    pulsator.stop()
//                    RelativeLoader.visibility = View.GONE
//
//                }, 1000)

            })


        })
    }

    private fun setPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
        }
    }

    private fun getDataFromApi() {
        val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)
        api.findAll().enqueue(object : Callback<List<FindAll>> {
            override fun onResponse(call: Call<List<FindAll>>, response: Response<List<FindAll>>) {

                stationList = response.body()!!

                for (FindAll in stationList) {


                    api.getIndex(FindAll.id.toString()).enqueue(object : Callback<ModelIndex> {

                        override fun onResponse(
                            call: Call<ModelIndex>,
                            response: Response<ModelIndex>
                        ) {

                            modelIndexList.add(response.body()!!)

                            //    Log.d("MainActivity1223:Code ", "Call  ${response.body()}")


                            Log.d("MainActivity8383:Code ", "Call  ${response.body()!!}")
                            iterator++;
                            println(iterator)
                            if (iterator==stationList.size){
                                getAllData();
                              //  Handler().postDelayed(Runnable {

                                    RelativeLoader.visibility = View.GONE

                              //  }, 0)
                            }
                        }

                        override fun onFailure(call: Call<ModelIndex>, t: Throwable) {
                            Log.d("MainActivity1313x:Code ", "Call  ${t.message}")

                        }

                    })

                }

            }

            override fun onFailure(call: Call<List<FindAll>>, t: Throwable) {
                Log.d("MainActivity1313Code", "Call  ${t.message}")

                RelativeLoader.visibility = View.GONE
            }


        })
    }


    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        var vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight()
        );
        var bitmap = Bitmap.createBitmap(
            vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888
        );
        var canvas = Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private fun getAllData() {
        for (FindAll in stationList) {
            var drawable: Int =
                R.drawable.circle

            for (modelIndex in modelIndexList)
                if (modelIndex.id.equals(FindAll.id)) {
                    if (modelIndex.pm10IndexLevel != null) {
                        when (modelIndex.pm10IndexLevel.id) {
                            0 -> {
                                drawable =
                                    R.drawable.circle
                            }
                            1 -> {
                                drawable =
                                    R.drawable.circle2
                            }
                            2 -> {
                                drawable =
                                    R.drawable.circle3
                            }
                            3 -> {
                                drawable =
                                    R.drawable.circle4
                            }
                            else -> {
                                drawable =
                                    R.drawable.circle
                            }
                        }
                    }

                    val location =
                        LatLng(FindAll.gegrLat.toDouble(), FindAll.gegrLon.toDouble())
                    if (modelIndex.pm10IndexLevel != null) {
                        googleMap.addMarker(

                            MarkerOptions().position(location).title(modelIndex.pm10IndexLevel.indexLevelName).icon(
                                bitmapDescriptorFromVector(
                                    requireContext(),
                                    drawable
                                )
                            )
                        )
                    }
                }
        }
    }
}





