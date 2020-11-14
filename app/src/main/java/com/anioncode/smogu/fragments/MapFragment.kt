package com.anioncode.smogu.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.anioncode.retrofit2.ApiService
import com.anioncode.smogu.adapter.SensorAdapter
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.CONST.MyVariables.Companion.SENSORNAME
import com.anioncode.smogu.CONST.MyVariables.Companion.modelIndexList
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorIDList
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorbyIDList
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorsNameList
import com.anioncode.smogu.CONST.MyVariables.Companion.stationList
import com.anioncode.smogu.model.ModelSensorId.SensorbyID
import com.anioncode.smogu.R
import com.anioncode.smogu.events.NavEvent
import com.anioncode.smogu.presenters.MapFragmentPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.custom_marker.view.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.RecyclerView
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment @Inject constructor() : Fragment(R.layout.fragment_map),
    MapFragmentPresenter.View {

    @Inject
    lateinit var navEvents: PublishProcessor<NavEvent>

    @Inject
    lateinit var loadingEvents: PublishProcessor<Boolean>

    @Inject
    lateinit var service: ApiService

    @Inject
    lateinit var adapterSensor: SensorAdapter

    var compositeDisposable: CompositeDisposable = CompositeDisposable()
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    lateinit var sensorIDListNotStatic: ArrayList<String>
    private val polandCoordinates = LatLngBounds(LatLng(47.0, 14.0), LatLng(56.5, 24.3))
    var iterator = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Initialize View events , set Marker on Map
        initView()
        //Initialize View events
        setMapFragment()


    }

    override fun initView() {

        RecyclerView.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = adapterSensor
        }
        pomiar.text = SENSORNAME
        image.setOnClickListener {
            if (show.visibility == View.VISIBLE) {
                show.visibility = View.INVISIBLE
            } else {
                show.visibility = View.VISIBLE
            }
        }

        st.setOnClickListener {
            SENSORNAME = "ST"
            refresh()
        }
        co.setOnClickListener {
            SENSORNAME = "CO"
            refresh()
        }
        pm10.setOnClickListener {
            SENSORNAME = "PM10"
            refresh()
        }
        pm25.setOnClickListener {
            SENSORNAME = "PM2.5"
            refresh()
        }
        no2.setOnClickListener {
            SENSORNAME = "NO2"
            refresh()
        }
        so2.setOnClickListener {
            SENSORNAME = "SO2"
            refresh()
        }
        o3.setOnClickListener {
            SENSORNAME = "O3"
            refresh()
        }
        c6h6.setOnClickListener {
            SENSORNAME = "C6H6"
            refresh()
        }
        fab.setOnClickListener {
            val location = CameraUpdateFactory.newLatLngBounds(polandCoordinates, 0)
            googleMap.animateCamera(location);
        }
        compositeDisposable.add(loadingEvents.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it) {
                    googleMap.clear()
                    setMarkerOnMap()
                }
            }, { it.printStackTrace() })
        )

        Handler().postDelayed(Runnable {
            setMarkerOnMap()
            if (context != null) {
                RelativeLoader.visibility = View.GONE
            }

        }, 1800)
    }

    private fun refresh() {
        googleMap.clear()
        pomiar.text = SENSORNAME
        if (show.visibility == View.VISIBLE) {
            show.visibility = View.INVISIBLE
        } else {
            show.visibility = View.VISIBLE
        }
        setMarkerOnMap()
    }

    @SuppressLint("MissingPermission")
    override fun setMapFragment() {
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

                        if (findAll.id == marker?.snippet?.toInt()) {
                            rel.visibility = View.VISIBLE

                            googleMap.setOnInfoWindowClickListener {
                            }

                            choose.setOnClickListener {
                                val myPreference = MyPreference(requireContext())
                                myPreference.setID(marker?.getSnippet())
                                myPreference.setSTATION_NAME(findAll.stationName)
                                myPreference.setSTATION_STREET(findAll.addressStreet)
                                myPreference.setSTATION_PROVINCE(findAll.city.commune.provinceName)
                                sensorIDList.clear()
                                sensorIDList = sensorIDListNotStatic
                                navEvents.onNext(NavEvent(NavEvent.Destination.StatsFragment))
                                Toast.makeText(activity, "Wybrano stację", Toast.LENGTH_LONG).show()

                            }

                            getDataStationSensor(findAll.id.toString())

                            myContentView.name.text = findAll.stationName
                            myContentView.provinceName.text = findAll.city.commune.provinceName
                            myContentView.adressStreet.text = findAll.addressStreet
                            myContentView.jakosc.text = marker?.title
                            var color: Int =
                                R.color.colorbdb

                            when (marker?.title) {
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
                            myContentView.jakosc.setTextColor(resources.getColor(color, null))
                            break
                        }

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

            googleMap.setMinZoomPreference(5.4f)

            googleMap.setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback {
                // Permission to access the location is missing. Show rationale and request permission
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(polandCoordinates, 0))
            })


        })
    }


    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        var vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        );
        var bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        );
        var canvas = Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    override fun setMarkerOnMap() {
        for (allStation in stationList) {
            var drawable: Int =
                R.drawable.circle
            for (modelIndex in modelIndexList)
                if (modelIndex.id.equals(allStation.id)) {
                    when (SENSORNAME) {
                        "PM10" -> {
                            if (modelIndex.pm10IndexLevel != null) {
                                drawable = drawableCircle(modelIndex.pm10IndexLevel.id)
                            }
                            val location =
                                LatLng(allStation.gegrLat.toDouble(), allStation.gegrLon.toDouble())
                            if (modelIndex.pm10IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location)
                                        .title(modelIndex.pm10IndexLevel.indexLevelName).snippet(
                                            allStation.id.toString()
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
                                drawable = drawableCircle(modelIndex.stIndexLevel.id)
                            }
                            val location =
                                LatLng(allStation.gegrLat.toDouble(), allStation.gegrLon.toDouble())
                            if (modelIndex.stIndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location)
                                        .title(modelIndex.stIndexLevel.indexLevelName).snippet(
                                            allStation.id.toString()
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
                                drawable = drawableCircle(modelIndex.pm25IndexLevel.id)
                            }
                            val location =
                                LatLng(allStation.gegrLat.toDouble(), allStation.gegrLon.toDouble())
                            if (modelIndex.pm25IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location)
                                        .title(modelIndex.pm25IndexLevel.indexLevelName).snippet(
                                            allStation.id.toString()
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
                                drawable = drawableCircle(modelIndex.coIndexLevel.id)
                            }
                            val location =
                                LatLng(allStation.gegrLat.toDouble(), allStation.gegrLon.toDouble())
                            if (modelIndex.coIndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location)
                                        .title(modelIndex.coIndexLevel.indexLevelName).snippet(
                                            allStation.id.toString()
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
                                drawable = drawableCircle(modelIndex.no2IndexLevel.id)
                            }

                            val location =
                                LatLng(allStation.gegrLat.toDouble(), allStation.gegrLon.toDouble())
                            if (modelIndex.no2IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location)
                                        .title(modelIndex.no2IndexLevel.indexLevelName).snippet(
                                            allStation.id.toString()
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
                                drawable = drawableCircle(modelIndex.so2IndexLevel.id)
                            }
                            val location =
                                LatLng(allStation.gegrLat.toDouble(), allStation.gegrLon.toDouble())
                            if (modelIndex.so2IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location)
                                        .title(modelIndex.so2IndexLevel.indexLevelName).snippet(
                                            allStation.id.toString()
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
                        "C6H6" -> {
                            if (modelIndex.c6h6IndexLevel != null) {
                                drawable = drawableCircle(modelIndex.c6h6IndexLevel.id)
                            }
                            val location =
                                LatLng(allStation.gegrLat.toDouble(), allStation.gegrLon.toDouble())
                            if (modelIndex.c6h6IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location)
                                        .title(modelIndex.c6h6IndexLevel.indexLevelName).snippet(
                                            allStation.id.toString()
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
                        "O3" -> {
                            if (modelIndex.o3IndexLevel != null) {
                                drawable = drawableCircle(modelIndex.o3IndexLevel.id)
                            }
                            val location =
                                LatLng(allStation.gegrLat.toDouble(), allStation.gegrLon.toDouble())
                            if (modelIndex.o3IndexLevel != null) {
                                googleMap.addMarker(

                                    MarkerOptions().position(location)
                                        .title(modelIndex.o3IndexLevel.indexLevelName).snippet(
                                            allStation.id.toString()
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

    private fun drawableCircle(id: Int): Int {
        var intDrawable: Int
        when (id) {
            0 -> {
                intDrawable =
                    R.drawable.circle
            }
            1 -> {
                intDrawable =
                    R.drawable.circle2
            }
            2 -> {
                intDrawable =
                    R.drawable.circle3
            }
            3 -> {
                intDrawable =
                    R.drawable.circle4
            }
            4 -> {
                intDrawable =
                    R.drawable.circle5
            }
            5 -> {
                intDrawable =
                    R.drawable.circle6
            }
            else -> {
                intDrawable =
                    R.drawable.circle7
            }
        }
        return intDrawable
    }

    private fun getDataStationSensor(id_select: String) {
        sensorbyIDList = ArrayList<SensorbyID>()
        compositeDisposable.add(
            service.getDataRX(id_select).subscribeOn(Schedulers.single()).subscribe({
                sensorsNameList = it
                sensorIDListNotStatic.clear()
                for (sensorsName in sensorsNameList) {
                    sensorIDListNotStatic.add(sensorsName.id.toString())
                    compositeDisposable.add(
                        service.getSensorRX(sensorsName.id.toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe({ sensor ->
                                sensorbyIDList.add(sensor)
                                if (sensorbyIDList.size > 0) {
                                    adapterSensor.setData(sensorbyIDList)
                                }
                            }, { sensorTh ->
                                sensorTh.printStackTrace()
                            })
                    )
                }
            }, {
                it.printStackTrace()
            })
        )
    }


    override fun onDestroyView() {
        compositeDisposable.dispose()
        super.onDestroyView()
        if (googleMap != null) {
            googleMap.clear()
        }
    }
}





