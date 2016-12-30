package io.github.yusaka39.fakegps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() {
    private lateinit var map: GoogleMap
    private var marker: Marker? = null

    private var providers: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val mapFragment: SupportMapFragment =
                this.supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync { this.onInitializeMap(it) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2357)
        } else {
            this.prepareTestProviders()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != 2357 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            this.finish()
            return
        }
        this.prepareTestProviders()
    }

    private fun prepareTestProviders() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        this.providers = lm.allProviders.filter {
            listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).contains(it)
        }
        this.providers.forEach {
            val provider = lm.getProvider(it)
            lm.addTestProvider(provider.name,
                               provider.requiresNetwork(),
                               provider.requiresSatellite(),
                               provider.requiresCell(),
                               provider.hasMonetaryCost(),
                               provider.supportsAltitude(),
                               provider.supportsSpeed(),
                               provider.supportsBearing(),
                               provider.powerRequirement,
                               provider.accuracy)
            lm.setTestProviderEnabled(it, true)
        }
    }

    private fun onInitializeMap(map: GoogleMap) {
        this.map = map
        map.setOnMapLongClickListener { latLng ->
            this.marker?.remove()
            val option = MarkerOptions()
            option.position(latLng)
            this.marker = map.addMarker(option)
            this.providers.forEach {
                val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = Location(it)
                location.latitude = latLng.latitude
                location.longitude = latLng.longitude
                location.accuracy = 0f
                location.time = System.currentTimeMillis()
                location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                lm.setTestProviderLocation(it, location)
            }
        }
    }
}
