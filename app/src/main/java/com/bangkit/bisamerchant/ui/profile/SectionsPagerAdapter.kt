package com.bangkit.bisamerchant.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        val fragment = LoyaltyFragment()
        fragment.arguments = Bundle().apply {
            putInt(LoyaltyFragment.ARG_POSITION, position)
        }
        return fragment

    }

    override fun getItemCount(): Int {
        return 3
    }
}