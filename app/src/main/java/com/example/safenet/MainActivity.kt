package com.example.safenet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.safenet.fragment.cyclone_main
import com.example.safenet.fragment.earthquake_main
import com.example.safenet.fragment.flood_main
import com.example.safenet.fragment.heat_wave_main
import com.example.safenet.fragment.marine_main
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

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

        // Check login status from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // User is already logged in, initialize the activity
            initializeActivity()
        } else {
            // User is not logged in, navigate to the LoginSignup activity
            val intent = Intent(this, LoginSignup::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initializeActivity() {
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
            } else if (items == R.id.nav_earthquake) {
                loadFragment(earthquake_main(), false)
            }else if (items == R.id.nav_marine){
                loadFragment(marine_main(),false)
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