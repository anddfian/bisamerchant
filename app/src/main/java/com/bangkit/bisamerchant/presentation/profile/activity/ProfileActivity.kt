package com.bangkit.bisamerchant.presentation.profile.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityProfileBinding
import com.bangkit.bisamerchant.presentation.profile.adapter.SectionsPagerAdapter
import com.bangkit.bisamerchant.presentation.profile.viewmodel.ProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTopAppBar()
        updateUI(profileViewModel)
        tabLayoutConnector()
    }

    private fun initTopAppBar() {
        setSupportActionBar(binding.profileTopAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Profile"
        }
    }

    private fun updateUI(
        profileViewModel: ProfileViewModel,
    ) {
        profileViewModel.getMerchantActive()
        profileViewModel.merchant.observe(this) { merchant ->
            binding.apply {
                tvMerchantName.text = merchant.merchantName
                tvMerchantAddress.text = merchant.merchantAddress
                tvMerchantEmail.text = merchant.email
                tvMerchantType.text = merchant.merchantType
                val progress = merchant.transactionCount?.toInt() ?: 0
                when (merchant.transactionCount) {
                    in 0L..99L -> {
                        tvMerchantLoyalty.text = getString(R.string.bronze_merchant)
                        loyaltyCount.text = merchant.transactionCount.toString()
                        progressIndicator.progress = if (progress > 0) progress * 100 / 100 else 0
                    }
                    in 100L..199L -> {
                        tvMerchantLoyalty.text = getString(R.string.silver_merchant)
                        loyaltyCount.text = merchant.transactionCount.toString()
                        loyaltyCountToNextLevel.text = getString(R.string.loyalty_target_gold)
                        progressIndicator.progress = progress * 100 / 200
                        loyaltyNow.text = getString(R.string.silver)
                        loyaltyNext.text = getString(R.string.gold)
                    }
                    else -> {
                        tvMerchantLoyalty.text = getString(R.string.gold_merchant)
                        loyaltyCount.text = merchant.transactionCount.toString()
                        loyaltyCountToNextLevel.visibility = View.INVISIBLE
                        progressIndicator.progress = progress * 100 / 300
                        loyaltyNow.text = getString(R.string.gold)
                        loyaltyNext.visibility = View.INVISIBLE
                    }
                }
            }
            Glide.with(binding.root)
                .load(merchant.merchantLogo)
                .placeholder(R.drawable.ic_loading_24)
                .into(binding.ivMerchantLogo)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun tabLayoutConnector() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.profile_tab_layout)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.bronze, R.string.silver, R.string.gold
        )
    }
}