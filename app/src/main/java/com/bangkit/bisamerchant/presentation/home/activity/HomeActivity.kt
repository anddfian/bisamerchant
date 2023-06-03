package com.bangkit.bisamerchant.presentation.home.activity

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.domain.home.model.Merchant
import com.bangkit.bisamerchant.databinding.ActivityHomeBinding
import com.bangkit.bisamerchant.databinding.MerchantAccountBottomSheetBinding
import com.bangkit.bisamerchant.presentation.history.activity.HistoryActivity
import com.bangkit.bisamerchant.presentation.home.adapter.MerchantAccountAdapter
import com.bangkit.bisamerchant.presentation.home.viewmodel.HomeViewModel
import com.bangkit.bisamerchant.presentation.home.adapter.SectionsPagerAdapter
import com.bangkit.bisamerchant.presentation.invoice.activity.DetailTransactionActivity
import com.bangkit.bisamerchant.presentation.utils.Utils
import com.bangkit.bisamerchant.presentation.profile.activity.ProfileActivity
import com.bangkit.bisamerchant.presentation.merchantregister.activity.MerchantRegisterActivity
import com.bangkit.bisamerchant.presentation.setting.activity.SettingActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private var _merchantAccountBottomSheetBinding: MerchantAccountBottomSheetBinding? = null
    private val merchantAccountBottomSheetBinding get() = _merchantAccountBottomSheetBinding!!

    private var _bottomSheetDialog: BottomSheetDialog? = null
    private val bottomSheetDialog get() = _bottomSheetDialog!!

    private var amount: Long? = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        triggerNotification()
        setupFilterBottomSheet()
        updateUI()
        initTopAppBar()
        initClickListener()
        tabLayoutConnector()
    }

    private fun setupFilterBottomSheet() {
        _merchantAccountBottomSheetBinding =
            MerchantAccountBottomSheetBinding.inflate(layoutInflater)
        _bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(merchantAccountBottomSheetBinding.root)
    }

    private fun triggerNotification() {
        homeViewModel.getTransactionsToday()
        homeViewModel.messageNotif.observe(this) { message ->
            Utils.sendNotification(
                context = this,
                contentIntent = contentIntent(),
                channelId = CHANNEL_ID,
                channelName = CHANNEL_NAME,
                notificationId = NOTIFICATION_ID,
                resources = resources,
                message = message,
            )
        }
    }

    private fun updateUI() {
        lifecycleScope.launch {
            launch {
                homeViewModel.getMerchantActive()
                homeViewModel.merchant.observe(this@HomeActivity) { merchant ->
                    homeViewModel.getHideAmount()
                    binding.apply {
                        tvMerchantName.text = merchant.merchantName?.let { Utils.truncateString(it, 20) }
                        amount = merchant.balance
                        homeViewModel.isAmountHide.observe(this@HomeActivity) {
                            if (it) {
                                btnHideAmount.visibility = View.GONE
                                btnShowAmount.visibility = View.VISIBLE
                                tvBalanceAmount.text = "***"
                            } else {
                                btnHideAmount.visibility = View.VISIBLE
                                btnShowAmount.visibility = View.GONE
                                tvBalanceAmount.text = Utils.currencyFormat(amount)
                            }
                        }
                    }
                }

            }
            launch {
                homeViewModel.getMerchants()
                homeViewModel.merchantsList.observe(this@HomeActivity) { merchantList ->
                    if (merchantList.isNotEmpty()) {
                        setTransactionsData(merchantList)
                    }
                }
            }
            showRecyclerMerchants()
        }
    }

    private fun initTopAppBar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = ""
    }

    private fun initClickListener() {
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this@HomeActivity, HistoryActivity::class.java))
        }

        binding.btnProfileMerchant.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
        }

        binding.btnShowAmount.setOnClickListener {
            binding.btnHideAmount.visibility = View.VISIBLE
            binding.btnShowAmount.visibility = View.GONE
            binding.tvBalanceAmount.text = Utils.currencyFormat(amount)
            homeViewModel.updateHideAmount(false)
        }

        binding.btnHideAmount.setOnClickListener {
            binding.btnHideAmount.visibility = View.GONE
            binding.btnShowAmount.visibility = View.VISIBLE
            binding.tvBalanceAmount.text = "***"
            homeViewModel.updateHideAmount(true)
        }

        binding.tvMerchantName.setOnClickListener {
            showRecyclerMerchants()
            bottomSheetDialog.show()
        }

        merchantAccountBottomSheetBinding.btnAddMerchant.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MerchantRegisterActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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

    private fun showRecyclerMerchants() {
        val layoutManager = LinearLayoutManager(this)
        merchantAccountBottomSheetBinding.rvMerchantList.layoutManager = layoutManager
    }

    private fun setTransactionsData(
        merchants: List<Merchant>
    ) {
        val listMerchants = ArrayList<Merchant>()
        for (merchant in merchants) {
            listMerchants.add(merchant)
        }
        val adapter = MerchantAccountAdapter(homeViewModel, listMerchants)
        merchantAccountBottomSheetBinding.rvMerchantList.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (_binding == null) {
            _merchantAccountBottomSheetBinding = null
        }
        bottomSheetDialog.dismiss()
    }

    private fun contentIntent(): PendingIntent {
        val notificationIntent = Intent(this, HistoryActivity::class.java)

        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            pendingFlags
        )
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "Bisa_Channel_01"
        private const val CHANNEL_NAME = "Bisa Channel"

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