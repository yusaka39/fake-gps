package io.github.yusaka39.fakegps.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.util.Timer
import kotlin.concurrent.timer

/**
 * Created by yusaka on 12/31/16.
 */

private val MOCKING_TARGET = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

class LocationProviderService :
        Service() {

    companion object {
        const val LAT_LNG_KEY = "latlng"
    }

    private var timer: Timer? = null

    override fun onBind(intent: Intent?): IBinder = object : Binder() {
        fun stopService() {
            this@LocationProviderService.stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val latlng = intent?.getParcelableExtra<LatLng>(LAT_LNG_KEY)
        if (latlng == null) {
            this.stopSelf()
            return Service.START_REDELIVER_INTENT
        }

        this.timer?.cancel()

        val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        this.prepareTestProviders(lm)
        this.timer = timer(period = 5000L) {
            Log.d(LocationProviderService::class.java.name, "Update location")
            MOCKING_TARGET.filter { lm.isProviderEnabled(it) }
                    .forEach { updateLocation(lm, it, latlng) }
        }
        return Service.START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        this.timer?.cancel()
    }

    private fun prepareTestProviders(lm: LocationManager) {
        MOCKING_TARGET.forEach {
            val provider = lm.getProvider(it) ?: return
            lm.addTestProvider(provider.name, provider.requiresNetwork(),
                               provider.requiresSatellite(), provider.requiresCell(),
                               provider.hasMonetaryCost(), provider.supportsAltitude(),
                               provider.supportsSpeed(), provider.supportsBearing(),
                               provider.powerRequirement, provider.accuracy)
            lm.setTestProviderEnabled(it, true)
        }
    }

    private fun updateLocation(lm: LocationManager, providerName: String, latLng : LatLng) {
        val location = Location(providerName)
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude
        location.accuracy = 0f
        location.time = System.currentTimeMillis()
        location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        lm.setTestProviderLocation(providerName, location)
    }
}