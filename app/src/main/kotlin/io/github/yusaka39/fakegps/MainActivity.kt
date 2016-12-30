package io.github.yusaka39.fakegps

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.SupportMapFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment: SupportMapFragment =
                this.supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
    }
}
