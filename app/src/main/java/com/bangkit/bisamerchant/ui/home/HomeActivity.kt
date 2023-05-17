package com.bangkit.bisamerchant.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityHomeBinding
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.Utils
import com.bangkit.bisamerchant.helper.ViewModelMerchantFactory
import com.bangkit.bisamerchant.ui.history.TransactionHistoryActivity
import com.bangkit.bisamerchant.ui.notification.NotificationActivity
import com.bangkit.bisamerchant.ui.profile.ProfileActivity
import com.bangkit.bisamerchant.ui.setting.SettingActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("merchant")

class HomeActivity : AppCompatActivity() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val merchantViewModel = initMerchantViewModel()
        Utils.getTodayTimestamp()
        updateUI(merchantViewModel)
        initTopAppBar()
        initClickListener()
        tabLayoutConnector()
    }

    private fun initMerchantViewModel(): MerchantViewModel {
        val pref = MerchantPreferences.getInstance(dataStore)
        val factory = ViewModelMerchantFactory.getInstance(pref)
        val merchantViewModel: MerchantViewModel by viewModels { factory }
        return merchantViewModel
    }

    private fun updateUI(
        merchantViewModel: MerchantViewModel,
    ) {
        merchantViewModel.getMerchantActive()
        merchantViewModel.merchant.observe(this) { merchant ->
            binding.apply {
                tvMerchantName.text = merchant.merchantName
                tvBalanceAmount.text = Utils.currencyFormat(merchant.balance)
            }
        }
    }

    private fun initTopAppBar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = ""
    }

    private fun initClickListener() {
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this@HomeActivity, TransactionHistoryActivity::class.java))
        }
        binding.btnProfileMerchant.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notification -> {
                startActivity(Intent(this@HomeActivity, NotificationActivity::class.java))
                true
            }

            R.id.action_setting -> {
                startActivity(Intent(this@HomeActivity, SettingActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun tabLayoutConnector() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.icon = ContextCompat.getDrawable(this, TAB_ICONS[position])
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
            R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3
        )

        @DrawableRes
        private val TAB_ICONS = intArrayOf(
            R.drawable.ic_transaction_24, R.drawable.ic_payment_24, R.drawable.ic_withdraw_24
        )
    }
}