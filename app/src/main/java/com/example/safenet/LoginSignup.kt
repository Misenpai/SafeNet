package com.example.safenet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.signlogintab.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class LoginSignup : AppCompatActivity() {

    private lateinit var tabLayout:TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_page)
        tabLayout.addTab(tabLayout.newTab().setText("Login"))
        tabLayout.addTab(tabLayout.newTab().setText("SignUp"))
        val fragmentManager:FragmentManager = supportFragmentManager
        adapter = ViewPagerAdapter(fragmentManager,lifecycle)
        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object :OnTabSelectedListener{
            override fun onTabSelected(tab:TabLayout.Tab){
                viewPager2.currentItem=tab.position
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }
            override fun onTabReselected(tab:TabLayout.Tab){

            }
        })

        viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(postion:Int){
                tabLayout.selectTab(tabLayout.getTabAt(postion))
            }
        })
    }
}