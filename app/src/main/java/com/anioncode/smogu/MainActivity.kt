package com.anioncode.smogu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.ModelAll.FindAll
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    lateinit var stationList: List<FindAll>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //variable
        val Poland = LatLngBounds(LatLng(48.0, 14.0), LatLng(57.5, 24.3))
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
        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment) as SupportMapFragment
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



            googleMap.setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback {

                // Permission to access the location is missing. Show rationale and request permission
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Poland, 0))
                Handler().postDelayed(Runnable {
                    pulsator.stop()
                    RelativeLoader.visibility = View.GONE

                }, 1000)

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

                stationList= response.body()!!

                for (FindAll in stationList){
                    val location=LatLng(FindAll.gegrLat.toDouble(),FindAll.gegrLon.toDouble())
                    googleMap.addMarker(MarkerOptions().position(location).title("this"))
                }
            }

            override fun onFailure(call: Call<List<FindAll>>, t: Throwable) {
                Log.d("MainActivity1313:Code ", "Call  ${t.message}")
            }


        })
    }
}




