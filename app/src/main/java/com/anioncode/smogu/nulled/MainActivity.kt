package com.anioncode.smogu.nulled

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.Model.ModelAll.FindAll
import com.anioncode.smogu.Model.ModelIndex.ModelIndex
import com.anioncode.smogu.Model.ModelSensor.SensorsName
import com.anioncode.smogu.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    lateinit var stationList: List<FindAll>
    lateinit var sensorsNameList: List<SensorsName>
    lateinit var modelIndexList: ArrayList<ModelIndex>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        modelIndexList = ArrayList<ModelIndex>()
        //variable
        val Poland = LatLngBounds(LatLng(47.0, 14.0), LatLng(56.5, 24.3))
        pulsator.start()



        getDataFromApi()

        setPermission()

        setMapFragment(Poland)

        fab.setOnClickListener(
            View.OnClickListener {
                val location = CameraUpdateFactory.newLatLngBounds(Poland, 0)
                // googleMap.moveCamera(location)
                googleMap.animateCamera(location);
            }
        )

    }

    private fun setMapFragment(Poland: LatLngBounds) {
        mapFragment = getSupportFragmentManager().findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap.isMyLocationEnabled = true
            try {
                val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
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

                var iterator = 0;
                for (FindAll in stationList) {
                    api.getIndex(FindAll.id.toString()).enqueue(object : Callback<ModelIndex> {

                        override fun onResponse(
                            call: Call<ModelIndex>,
                            response: Response<ModelIndex>
                        ) {

                            //    Log.d("MainActivity1223:Code ", "Call  ${response.body()}")
                            var drawable: Int =
                                R.drawable.circle
                            modelIndexList.add(response.body()!!)


                            if (response.body()!!.pm10IndexLevel != null) {
                                when (response.body()!!.pm10IndexLevel.id) {
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
                            if (response.body()!!.pm10IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location).title(response.body()!!.pm10IndexLevel.indexLevelName).icon(
                                        bitmapDescriptorFromVector(
                                            this@MainActivity,
                                            drawable
                                        )
                                    )
                                )
                            }


                            Log.d("MainActivity8383:Code ", "Call  ${response.body()!!}")
                        }

                        override fun onFailure(call: Call<ModelIndex>, t: Throwable) {
                            Log.d("MainActivity1313x:Code ", "Call  ${t.message}")

                        }

                    })

                    //  val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)
//                    api.getData(FindAll.id.toString())
//                        .enqueue(object : Callback<List<SensorsName>> {
//                            override fun onResponse(
//                                call: Call<List<SensorsName>>,
//                                response: Response<List<SensorsName>>
//                            ) {
//                                //    Log.d("MainActivity1223:Code ", "Call  ${response.body()}")
//
//                                sensorsNameList = response.body()!!
//
//                            }
//
//                            override fun onFailure(call: Call<List<SensorsName>>, t: Throwable) {
//                                Log.d("MainActivity1313:Code ", "Call  ${t.message}")
//                            }
//
//                        })
//                    var icon: BitmapDescriptor? =
//                        BitmapDescriptorFactory.fromResource(R.drawable.circle)

                    iterator++;
                    if (iterator == stationList.size) {
                        Handler().postDelayed(Runnable {
                            pulsator.stop()
                            RelativeLoader.visibility = View.GONE

                        }, 1500)
                    }
                }

            }

            override fun onFailure(call: Call<List<FindAll>>, t: Throwable) {
                Log.d("MainActivity1313Code", "Call  ${t.message}")
                pulsator.stop()
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

}




