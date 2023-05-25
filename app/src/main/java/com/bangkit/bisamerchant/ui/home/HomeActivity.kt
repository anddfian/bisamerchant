package com.bangkit.bisamerchant.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.data.response.Merchant
import com.bangkit.bisamerchant.databinding.ActivityHomeBinding
import com.bangkit.bisamerchant.databinding.MerchantAccountBottomSheetBinding
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.Utils
import com.bangkit.bisamerchant.helper.ViewModelMerchantFactory
import com.bangkit.bisamerchant.helper.ViewModelTransactionFactory
import com.bangkit.bisamerchant.ui.history.TransactionHistoryActivity
import com.bangkit.bisamerchant.ui.profile.ProfileActivity
import com.bangkit.bisamerchant.ui.register.MerchantRegisterActivity
import com.bangkit.bisamerchant.ui.setting.SettingActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data")

class HomeActivity : AppCompatActivity() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!


    private var _merchantAccountBottomSheetBinding: MerchantAccountBottomSheetBinding? = null
    private val merchantAccountBottomSheetBinding get() = _merchantAccountBottomSheetBinding!!

    private var _bottomSheetDialog: BottomSheetDialog? = null
    private val bottomSheetDialog get() = _bottomSheetDialog!!
    private var amount: Long? = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFilterBottomSheet()
        val merchantViewModel = initMerchantViewModel()
        updateUI(merchantViewModel)
        initTopAppBar()
        initClickListener(merchantViewModel)
        tabLayoutConnector()
    }

    private fun initMerchantViewModel(): MerchantViewModel {
        val pref = MerchantPreferences.getInstance(dataStore)
        val factory = ViewModelMerchantFactory.getInstance(pref)
        val merchantViewModel: MerchantViewModel by viewModels { factory }
        return merchantViewModel
    }

    private fun setupFilterBottomSheet() {
        _merchantAccountBottomSheetBinding =
            MerchantAccountBottomSheetBinding.inflate(layoutInflater)
        _bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(merchantAccountBottomSheetBinding.root)
    }

    private fun updateUI(
        merchantViewModel: MerchantViewModel,
    ) {
        lifecycleScope.launch {
            launch {
                merchantViewModel.observeMerchantActive()
                merchantViewModel.merchant.observe(this@HomeActivity) { merchant ->
                    merchantViewModel.getAmountHide()
                    binding.apply {
                        tvMerchantName.text = merchant.merchantName?.let { Utils.truncateString(it, 20) }
                        amount = merchant.balance
                        merchantViewModel.isAmountHide.observe(this@HomeActivity) {
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
                merchantViewModel.observeMerchants()
                merchantViewModel.merchantsList.observe(this@HomeActivity) { merchantList ->
                    if (merchantList.isNotEmpty()) {
                        setTransactionsData(merchantViewModel, merchantList)
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

    private fun initClickListener(merchantViewModel: MerchantViewModel) {
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this@HomeActivity, TransactionHistoryActivity::class.java))
        }
        binding.btnProfileMerchant.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
        }

        binding.btnShowAmount.setOnClickListener {
            binding.btnHideAmount.visibility = View.VISIBLE
            binding.btnShowAmount.visibility = View.GONE
            binding.tvBalanceAmount.text = Utils.currencyFormat(amount)
            merchantViewModel.saveAmountHide(false)
        }

        binding.btnHideAmount.setOnClickListener {
            binding.btnHideAmount.visibility = View.GONE
            binding.btnShowAmount.visibility = View.VISIBLE
            binding.tvBalanceAmount.text = "***"
            merchantViewModel.saveAmountHide(true)
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
        merchantViewModel: MerchantViewModel,
        merchants: List<Merchant>
    ) {
        val listMerchants = ArrayList<Merchant>()
        for (merchant in merchants) {
            listMerchants.add(merchant)
        }
        val adapter = MerchantAccountAdapter(merchantViewModel, listMerchants)
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