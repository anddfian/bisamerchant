package com.bangkit.bisamerchant.presentation.profile.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bangkit.bisamerchant.presentation.profile.activity.fragment.LoyaltyFragment

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