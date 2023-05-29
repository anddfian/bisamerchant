package com.bangkit.bisamerchant.ui.home

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bangkit.bisamerchant.ui.home.payment.PaymentFragment
import com.bangkit.bisamerchant.ui.home.transaction.TransactionFragment
import com.bangkit.bisamerchant.ui.home.withdraw.WithdrawFragment

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