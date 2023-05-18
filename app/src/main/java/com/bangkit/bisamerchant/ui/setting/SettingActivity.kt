package com.bangkit.bisamerchant.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivitySettingBinding
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.ViewModelMerchantFactory
import com.bangkit.bisamerchant.services.Auth
import com.bangkit.bisamerchant.ui.faq.FaqActivity
import com.bangkit.bisamerchant.ui.home.MerchantViewModel
import com.bangkit.bisamerchant.ui.termpolicy.PrivacyPolicyActivity
import com.bangkit.bisamerchant.ui.termpolicy.TermConditionActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("merchant")

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivitySettingBinding? = null
    private val binding get() = _binding!!
    private var _merchantViewModel: MerchantViewModel? = null
    private val merchantViewModel get() = _merchantViewModel!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTopAppBar()
        setupClickListeners()
        _merchantViewModel = initMerchantViewModel()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cv_setting1 -> {
                startActivity(Intent(this@SettingActivity, MerchantSettingActivity::class.java))
            }
            R.id.cv_setting2 -> {
                startActivity(Intent(this@SettingActivity, TermConditionActivity::class.java))
            }
            R.id.cv_setting3 -> {
                startActivity(Intent(this@SettingActivity, PrivacyPolicyActivity::class.java))
            }
            R.id.cv_setting4 -> {
                startActivity(Intent(this@SettingActivity, FaqActivity::class.java))
            }
            R.id.cv_setting5 -> {
                TODO("QR-039")
            }
            R.id.btn_logout -> {
                merchantViewModel.observeMerchantActive()
                Auth.logout(this, this@SettingActivity)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _merchantViewModel = null
    }

    private fun initTopAppBar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Setting"
        }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener(this)
        binding.cvSetting1.setOnClickListener(this)
        binding.cvSetting2.setOnClickListener(this)
        binding.cvSetting3.setOnClickListener(this)
        binding.cvSetting4.setOnClickListener(this)
        binding.cvSetting5.setOnClickListener(this)
    }

    private fun initMerchantViewModel(): MerchantViewModel {
        val pref = MerchantPreferences.getInstance(dataStore)
        val factory = ViewModelMerchantFactory.getInstance(pref)
        val merchantViewModel: MerchantViewModel by viewModels { factory }
        return merchantViewModel
    }
}