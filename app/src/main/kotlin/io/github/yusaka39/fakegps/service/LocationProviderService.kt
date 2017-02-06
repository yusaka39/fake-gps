package io.github.yusaka39.fakegps.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.Timer
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.timer

/**
 * Created by yusaka on 12/31/16.
 */

private val MOCKING_TARGET = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

class LocationProviderService :
        Service() {

    inner class LocationBinder : Binder() {
        fun getServiceInstance() = this@LocationProviderService
    }

    companion object {
        const val LAT_LNG_KEY = "latlng"
    }

    private var timer: Timer? = null
    private var apiClient: GoogleApiClient? = null

    override fun onBind(intent: Intent?): IBinder = LocationBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val latlng = intent?.getParcelableExtra<LatLng>(LAT_LNG_KEY)
        if (latlng == null) {
            this.stopSelf()
            return Service.START_REDELIVER_INTENT
        }

        this.timer?.cancel()
        this.disconnectClientIfNeeded()

        this.apiClient = buildApiClient(latlng)
        this.apiClient?.connect()

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
        this.timer?.cancel()
        this.disconnectClientIfNeeded()
        super.onDestroy()
    }

    private fun disconnectClientIfNeeded() {
        val client = this.apiClient ?: return
        if (client.isConnected || client.isConnecting) {
            client.disconnect()
        }
    }

    private fun buildApiClient(latlng: LatLng) =
            GoogleApiClient.Builder(this).addApi(LocationServices.API)
                    .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                        private var timer: Timer? = null

                        override fun onConnected(p0: Bundle?) {
                            val api = LocationServices.FusedLocationApi
                            api.setMockMode(this@LocationProviderService.apiClient, true)

                            this.timer = timer(period = 5000L) {
                                MOCKING_TARGET.forEach {
                                    api.setMockLocation(this@LocationProviderService.apiClient,
                                                        this@LocationProviderService
                                                                .createLocationFor(it, latlng))

                                }
                            }
                        }

                        override fun onConnectionSuspended(p0: Int) {
                            this.timer?.cancel()
                        }
                    })
                    .build()

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

    private fun createLocationFor(provider: String, latLng: LatLng) = Location(provider).apply {
        this.latitude = latLng.latitude
        this.longitude = latLng.longitude
        this.accuracy = 0f
        this.time = System.currentTimeMillis()
        this.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
    }

    private fun updateLocation(lm: LocationManager, providerName: String, latLng : LatLng) {
        lm.setTestProviderLocation(providerName, this.createLocationFor(providerName, latLng))
    }
}