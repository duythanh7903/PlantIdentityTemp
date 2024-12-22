package com.plantcare.ai.identifier.plantid.ui.component.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar.HomeFragment
import com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar.MyPlantFragment

class MainAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> HomeFragment()
            else -> MyPlantFragment()
        }
}