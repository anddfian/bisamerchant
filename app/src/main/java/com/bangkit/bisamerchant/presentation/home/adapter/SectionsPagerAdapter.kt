package com.bangkit.bisamerchant.presentation.home.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bangkit.bisamerchant.presentation.home.activity.fragment.payment.PaymentFragment
import com.bangkit.bisamerchant.presentation.home.activity.fragment.transaction.TransactionFragment
import com.bangkit.bisamerchant.presentation.home.activity.fragment.withdraw.WithdrawFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = TransactionFragment()
            1 -> fragment = PaymentFragment()
            2 -> fragment = WithdrawFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 3
    }
}