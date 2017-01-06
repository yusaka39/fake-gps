package io.github.yusaka39.fakegps.activity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.github.yusaka39.fakegps.R
import io.github.yusaka39.fakegps.service.LocationProviderService
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var map: GoogleMap
    private var marker: Marker? by Delegates.observable(null) { p, o: Marker?, n: Marker? ->
        n?.position?.let {
            this.startLocationUpdatingService(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.activity_main)
        val mapFragment: SupportMapFragment =
                this.supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync { this.onInitializeMap(it) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                       "MOCK_LOCATION"),
                               2357)
        }
        findViewById(R.id.stopButton).setOnClickListener {
            stopService(Intent(this, LocationProviderService::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != 2357 || grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
            Snackbar.make(this.findViewById(R.id.activity_main),
                          "permission denied", Snackbar.LENGTH_LONG).show()
            //this.finish()
        }
    }

    private fun onInitializeMap(map: GoogleMap) {
        this.map = map
        map.setOnMapLongClickListener { latLng ->
            this.marker?.remove()
            val option = MarkerOptions()
            option.position(latLng)
            this.marker = map.addMarker(option)
        }
    }

    fun startLocationUpdatingService(latLng : LatLng) {
        val intent = Intent(this, LocationProviderService::class.java)
        intent.putExtra(LocationProviderService.LAT_LNG_KEY, latLng)
        this.startService(intent)
    }
}
