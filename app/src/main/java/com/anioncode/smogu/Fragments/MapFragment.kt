package com.anioncode.smogu.Fragments

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.util.Log.i
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.Adapter.SensorAdapter
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.CONST.MyVariables
import com.anioncode.smogu.CONST.MyVariables.Companion.SENSORNAME
import com.anioncode.smogu.CONST.MyVariables.Companion.modelIndexList
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorIDList
import com.anioncode.smogu.CONST.MyVariables.Companion.stationList
import com.anioncode.smogu.Model.ModelSensor.SensorsName
import com.anioncode.smogu.Model.ModelSensorId.SensorbyID
import com.anioncode.smogu.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.custom_marker.view.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.RecyclerView
import kotlinx.android.synthetic.main.fragment_map.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() {

    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap

    lateinit var sensorIDListNotStatic: ArrayList<String>
    //    lateinit var stationList: List<FindAll>
//    lateinit var sensorsNameList: List<SensorsName>
//    lateinit var modelIndexList: ArrayList<ModelIndex>
    var iterator = 0;

//    fun newInstance(stan: String): MapFragment {
//        val args: Bundle = Bundle()
//        args.putSerializable("stan", stan)
//        val fragment = MapFragment()
//        fragment.arguments = args
//        return fragment
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_map, container, false)

        view.pomiar.setText(SENSORNAME)
        view.image.setOnClickListener {
            if (view.show.visibility == View.VISIBLE) {
                view.show.visibility = View.INVISIBLE
            } else {
                view.show.visibility = View.VISIBLE
            }
        }
//        if (getArguments() != null) {
//            SENSORNAME = getArguments()!!.getString("stan").toString();
//
//        }
        //variable
        val Poland = LatLngBounds(LatLng(47.0, 14.0), LatLng(56.5, 24.3))

        view.st.setOnClickListener {
            SENSORNAME = "ST"
            refresh()
        }
        view.co.setOnClickListener {
            SENSORNAME = "CO"
            refresh()
        }
        view.pm10.setOnClickListener {
            SENSORNAME = "PM10"
            refresh()
        }
        view.pm25.setOnClickListener {
            SENSORNAME = "PM2.5"
            refresh()
        }
        view.no2.setOnClickListener {
            SENSORNAME = "NO2"
            refresh()
        }
        view.so2.setOnClickListener {
            SENSORNAME = "SO2"
            refresh()
        }


        /////////COMMENT________________START
//        if (modelIndexList.size == 0 || (sizedApplication) > 0 && sizedApplication != stationList.size) {
////        if (modelIndexList.size == 0) {
//
//            if (stationList.size > 0) {
//                print("TAK $sizedApplication  ${stationList.size}  ")
//                modelIndexList.clear()
//                stationList = emptyList()
//
//            }
//
//            view.progress.visibility = View.VISIBLE
//            getDataFromApi()
//            setPermission()
//            setMapFragment(Poland)
//
//        } else {


//        }
        /////////COMMENT________________END

        setMapFragment(Poland)
        Handler().postDelayed(Runnable {
            getAllData();
            if (context != null) {
                RelativeLoader.visibility = View.GONE
            }


        }, 1800)
        view.fab.setOnClickListener(
            View.OnClickListener {
                val location = CameraUpdateFactory.newLatLngBounds(Poland, 0)
                // googleMap.moveCamera(location)
                googleMap.animateCamera(location);
            }
        )

        return view
    }

    private fun refresh() {
        var frg: Fragment?
        frg = fragmentManager!!.findFragmentByTag("SOMETAG")
        val ft: FragmentTransaction =
            fragmentManager!!.beginTransaction()
        frg?.let { ft.detach(it) }
        frg?.let { ft.attach(it) }
        ft.commit()
    }

    private fun setMapFragment(Poland: LatLngBounds) {
        sensorIDListNotStatic = ArrayList<String>()
        mapFragment = childFragmentManager.findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap.isMyLocationEnabled = true
            googleMap.setOnMapClickListener {
                rel.visibility = View.GONE

            }
            googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

                override fun getInfoContents(marker: Marker?): View {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun getInfoWindow(marker: Marker?): View {
                    var myContentView: View = getLayoutInflater().inflate(
                        R.layout.custom_marker, null
                    )

                    for (findAll in stationList) {

                        if (findAll.id == marker?.getSnippet()?.toInt()) {
                            rel.visibility = View.VISIBLE

                            googleMap.setOnInfoWindowClickListener {
                            }

                            choose.setOnClickListener {
                                val myPreference: MyPreference = MyPreference(requireContext())
                                myPreference.setID(marker?.getSnippet())
                                myPreference.setSTATION_NAME(findAll.stationName)
                                myPreference.setSTATION_STREET(findAll.addressStreet)
                                myPreference.setSTATION_PROVINCE(findAll.city.commune.provinceName)
                                // myPreference.setSENSORID(sensorId)
                                sensorIDList.clear()
                                sensorIDList = sensorIDListNotStatic

                                getFragmentManager()?.beginTransaction()
                                    ?.replace(R.id.fragment, StatsFragment(), "SOMETAG")?.commit();

                                Toast.makeText(activity, "Wybrano stację", Toast.LENGTH_LONG).show()

                            }

                            getDataStationSensor(findAll.id.toString())
                            myContentView.name.setText(findAll.stationName)
                            myContentView.provinceName.setText(findAll.city.commune.provinceName)
                            myContentView.adressStreet.setText(findAll.addressStreet)
                            myContentView.jakosc.setText(marker?.getTitle())


                            var color: Int =
                                R.color.colorbdb

                            when (marker?.getTitle()) {
                                "Bardzo dobry" -> {
                                    color =
                                        R.color.colorbdb
                                }
                                "Dobry" -> {
                                    color =
                                        R.color.colordb
                                }
                                "Umiarkowany" -> {
                                    color =
                                        R.color.colordst
                                }
                                "Dostateczny" -> {
                                    color =
                                        R.color.colordop
                                }
                                "Zły" -> {
                                    color =
                                        R.color.colorndst
                                }
                                "Bardzo zły" -> {
                                    color =
                                        R.color.colorndstx
                                }
                                else -> {
                                    color =
                                        R.color.colorndst
                                }
                            }
                            myContentView.jakosc.setTextColor(resources.getColor(color))
                            break;
                        }
//                        findAll.stationName

                    }

                    return myContentView
                }

            })

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
            })


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
                    when (SENSORNAME) {
                        "PM10" -> {
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
                                    4 -> {
                                        drawable =
                                            R.drawable.circle5
                                    }
                                    5 -> {
                                        drawable =
                                            R.drawable.circle6
                                    }
                                    else -> {
                                        drawable =
                                            R.drawable.circle7
                                    }
                                }
                            }
                            val location =
                                LatLng(FindAll.gegrLat.toDouble(), FindAll.gegrLon.toDouble())
                            if (modelIndex.pm10IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location).title(modelIndex.pm10IndexLevel.indexLevelName).snippet(
                                        FindAll.id.toString()
                                    ).icon(
                                        context?.let {
                                            bitmapDescriptorFromVector(
                                                it,
                                                drawable
                                            )
                                        }
                                    )
                                )

                            }
                        }
                        "ST" -> {
                            if (modelIndex.stIndexLevel != null) {
                                when (modelIndex.stIndexLevel.id) {
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
                                    4 -> {
                                        drawable =
                                            R.drawable.circle5
                                    }
                                    5 -> {
                                        drawable =
                                            R.drawable.circle6
                                    }
                                    else -> {
                                        drawable =
                                            R.drawable.circle7
                                    }
                                }
                            }
                            val location =
                                LatLng(FindAll.gegrLat.toDouble(), FindAll.gegrLon.toDouble())
                            if (modelIndex.stIndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location).title(modelIndex.stIndexLevel.indexLevelName).snippet(
                                        FindAll.id.toString()
                                    ).icon(
                                        context?.let {
                                            bitmapDescriptorFromVector(
                                                it,
                                                drawable
                                            )
                                        }
                                    )
                                )

                            }
                        }
                        "PM2.5" -> {
                            if (modelIndex.pm25IndexLevel != null) {
                                when (modelIndex.pm25IndexLevel.id) {
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
                                    4 -> {
                                        drawable =
                                            R.drawable.circle5
                                    }
                                    5 -> {
                                        drawable =
                                            R.drawable.circle6
                                    }
                                    else -> {
                                        drawable =
                                            R.drawable.circle7
                                    }
                                }
                            }
                            val location =
                                LatLng(FindAll.gegrLat.toDouble(), FindAll.gegrLon.toDouble())
                            if (modelIndex.pm25IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location).title(modelIndex.pm25IndexLevel.indexLevelName).snippet(
                                        FindAll.id.toString()
                                    ).icon(
                                        context?.let {
                                            bitmapDescriptorFromVector(
                                                it,
                                                drawable
                                            )
                                        }
                                    )
                                )

                            }
                        }
                        "CO" -> {
                            if (modelIndex.coIndexLevel != null) {
                                when (modelIndex.coIndexLevel.id) {
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
                                    4 -> {
                                        drawable =
                                            R.drawable.circle5
                                    }
                                    5 -> {
                                        drawable =
                                            R.drawable.circle6
                                    }
                                    else -> {
                                        drawable =
                                            R.drawable.circle7
                                    }
                                }
                            }
                            val location =
                                LatLng(FindAll.gegrLat.toDouble(), FindAll.gegrLon.toDouble())
                            if (modelIndex.coIndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location).title(modelIndex.coIndexLevel.indexLevelName).snippet(
                                        FindAll.id.toString()
                                    ).icon(
                                        context?.let {
                                            bitmapDescriptorFromVector(
                                                it,
                                                drawable
                                            )
                                        }
                                    )
                                )

                            }
                        }
                        "NO2" -> {
                            if (modelIndex.no2IndexLevel != null) {
                                when (modelIndex.no2IndexLevel.id) {
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
                                    4 -> {
                                        drawable =
                                            R.drawable.circle5
                                    }
                                    5 -> {
                                        drawable =
                                            R.drawable.circle6
                                    }
                                    else -> {
                                        drawable =
                                            R.drawable.circle7
                                    }
                                }
                            }
                            val location =
                                LatLng(FindAll.gegrLat.toDouble(), FindAll.gegrLon.toDouble())
                            if (modelIndex.no2IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location).title(modelIndex.no2IndexLevel.indexLevelName).snippet(
                                        FindAll.id.toString()
                                    ).icon(
                                        context?.let {
                                            bitmapDescriptorFromVector(
                                                it,
                                                drawable
                                            )
                                        }
                                    )
                                )

                            }
                        }
                        "SO2" -> {
                            if (modelIndex.so2IndexLevel != null) {
                                when (modelIndex.so2IndexLevel.id) {
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
                                    4 -> {
                                        drawable =
                                            R.drawable.circle5
                                    }
                                    5 -> {
                                        drawable =
                                            R.drawable.circle6
                                    }
                                    else -> {
                                        drawable =
                                            R.drawable.circle7
                                    }
                                }
                            }
                            val location =
                                LatLng(FindAll.gegrLat.toDouble(), FindAll.gegrLon.toDouble())
                            if (modelIndex.so2IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location).title(modelIndex.so2IndexLevel.indexLevelName).snippet(
                                        FindAll.id.toString()
                                    ).icon(
                                        context?.let {
                                            bitmapDescriptorFromVector(
                                                it,
                                                drawable
                                            )
                                        }
                                    )
                                )

                            }
                        }
                    }


                }
        }
    }

    private fun getDataStationSensor(id_select: String) {
        MyVariables.sensorbyIDList = ArrayList<SensorbyID>()//inicjalizacja danych mapy


        val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)

        api.getData(id_select).enqueue(object : Callback<List<SensorsName>> {

            override fun onResponse(
                call: Call<List<SensorsName>>,
                response: Response<List<SensorsName>>
            ) {

                MyVariables.sensorsNameList = response.body()!!
                sensorIDListNotStatic.clear()
                activity?.runOnUiThread {
                    for (sensorsName in MyVariables.sensorsNameList) {

                        sensorIDListNotStatic.add(sensorsName.id.toString())
//                        if(sensorsName.param.paramCode.equals("PM10")){
//                            sensorId=sensorsName.id.toString()
//                        }

                        api.getSensor(sensorsName.id.toString())
                            .enqueue(object : Callback<SensorbyID> {

                                override fun onResponse(
                                    call: Call<SensorbyID>,
                                    response: Response<SensorbyID>
                                ) {
                                    MyVariables.sensorbyIDList.add(response.body()!!)


                                    if (MyVariables.sensorbyIDList.size > 0) {


                                        activity?.runOnUiThread {
                                            RecyclerView.apply {
                                                layoutManager = LinearLayoutManager(
                                                    activity,
                                                    LinearLayoutManager.HORIZONTAL,
                                                    false
                                                )
                                                adapter = SensorAdapter(
                                                    MyVariables.sensorbyIDList,
                                                    activity!!,
                                                    clickListner = object :
                                                        SensorAdapter.OnItemClickListner {
                                                        override fun onItemClick(model: SensorbyID) {

                                                        }

                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<SensorbyID>, t: Throwable) {

                                }

                            })
                    }
                }

            }

            override fun onFailure(call: Call<List<SensorsName>>, t: Throwable) {

            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (googleMap != null) {
            googleMap.clear()
        }
    }
//    private fun getDataFromApi() {
//
//        val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)
//        api.findAll().enqueue(object : Callback<List<FindAll>> {
//            override fun onResponse(call: Call<List<FindAll>>, response: Response<List<FindAll>>) {
//
//                stationList = response.body()!!
//                sizedApplication = stationList.size
//                for (FindAll in stationList) {
//
//
//                    api.getIndex(FindAll.id.toString()).enqueue(object : Callback<ModelIndex> {
//
//                        override fun onResponse(
//                            call: Call<ModelIndex>,
//                            response: Response<ModelIndex>
//                        ) {
//
//                            modelIndexList.add(response.body()!!)
//
//                            //    Log.d("MainActivity1223:Code ", "Call  ${response.body()}")
//
//
//                            Log.d("MainActivity:Code ", "Call  ${response.body()!!}")
//                            iterator++;
//                            println(iterator)
//                            if (iterator == stationList.size) {
//                                if (context != null) {
//                                    getAllData();
//
//                                    //  Handler().postDelayed(Runnable {
//
//                                    RelativeLoader.visibility = View.GONE
//
//                                    //  }, 0)
//                                }
//                            }
//                            var pr: Double =
//                                Math.round(iterator / stationList.size.toDouble() * 100.0)
//                                    .toDouble()
//                            activity?.runOnUiThread {
//                                progress.text = pr.toInt().toString() + "%";
//                            }
//
//                        }
//
//                        override fun onFailure(call: Call<ModelIndex>, t: Throwable) {
//                            Log.d("MainActivity1313x:Code ", "Call  ${t.message}")
//
//                        }
//
//                    })
//
//                }
//
//            }
//
//            override fun onFailure(call: Call<List<FindAll>>, t: Throwable) {
//                Log.d("MainActivity1313Code", "Call  ${t.message}")
//
//                RelativeLoader.visibility = View.GONE
//            }
//
//
//        })
//    }
}





