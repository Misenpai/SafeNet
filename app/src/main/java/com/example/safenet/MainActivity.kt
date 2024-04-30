package com.example.safenet

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.safenet.fragment.cyclone_main
import com.example.safenet.fragment.flood_main
import com.example.safenet.fragment.heat_wave_main
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport

class MainActivity : AppCompatActivity(), PermissionsListener {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var frameLayout: FrameLayout
    lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigation = findViewById(R.id.bottomNavView)
        frameLayout = findViewById(R.id.frame_layout_main)



        loadFragment(heat_wave_main(), true)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val items = item.itemId

            if (items == R.id.nav_heat) {
                loadFragment(heat_wave_main(), false)
            } else if (items == R.id.nav_cyclone) {
                loadFragment(cyclone_main(), false)
            } else if (items == R.id.nav_flood) {
                loadFragment(flood_main(), false)
            }
            true
        }

        permissionsManager = PermissionsManager(this)
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager.requestLocationPermissions(this)
        }
    }




    private fun loadFragment(fragment: Fragment, isActive: Boolean) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        if (isActive) {
            fragmentTransaction.add(R.id.frame_layout_main, fragment)
        } else {
            fragmentTransaction.replace(R.id.frame_layout_main, fragment)
        }

        fragmentTransaction.commit()
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        TODO("Not yet implemented")
    }

    override fun onPermissionResult(granted: Boolean) {
        TODO("Not yet implemented")
    }
}