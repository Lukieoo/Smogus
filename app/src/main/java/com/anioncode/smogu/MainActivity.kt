package com.anioncode.smogu

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity() {

    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
        }
        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap.isMyLocationEnabled=true
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
            //  googleMap.setMinZoomPreference(6.0f);
            // googleMap.setMaxZoomPreference(14.0f);


            googleMap.setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback {

                // Permission to access the location is missing. Show rationale and request permission
             //   val AUSTRALIA = LatLngBounds(LatLng(-44.0, 113.0), LatLng(-10.0, 154.0))
               // googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(AUSTRALIA, 0))

            })


        })


    }
}




