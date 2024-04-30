package com.example.signlogintab

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.safenet.fragment.fragment_login
import com.example.safenet.fragment.fragment_signup

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return if (position == 1) {
            fragment_signup()
        } else {
            fragment_login()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}
