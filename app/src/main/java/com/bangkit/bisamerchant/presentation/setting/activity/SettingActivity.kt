package com.bangkit.bisamerchant.presentation.setting.activity

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivitySettingBinding
import com.bangkit.bisamerchant.presentation.faq.FaqActivity
import com.bangkit.bisamerchant.presentation.login.activity.LoginActivity
import com.bangkit.bisamerchant.presentation.merchantsetting.activity.MerchantSettingActivity
import com.bangkit.bisamerchant.presentation.setting.viewmodel.SettingViewModel
import com.bangkit.bisamerchant.presentation.termpolicy.PrivacyPolicyActivity
import com.bangkit.bisamerchant.presentation.termpolicy.TermConditionActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivitySettingBinding? = null
    private val binding get() = _binding!!

    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTopAppBar()
        updateUI()
        setupClickListeners()
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
                showDialog()
            }

            R.id.btn_logout -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.logout_confirmation))
                    .setMessage(getString(R.string.logout_confirmation_message))
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(getString(R.string.logout)) { _, _ ->
                        settingViewModel.logout()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        this.startActivity(intent)
                        finish()
                    }
                    .show()
            }
        }
    }

    private fun updateUI() {
        settingViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        settingViewModel.message.observe(this) {
            Toast.makeText(this@SettingActivity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
        binding.apply {
            cvSetting1.setOnClickListener(this@SettingActivity)
            cvSetting2.setOnClickListener(this@SettingActivity)
            cvSetting3.setOnClickListener(this@SettingActivity)
            cvSetting4.setOnClickListener(this@SettingActivity)
            cvSetting5.setOnClickListener(this@SettingActivity)
            btnLogout.setOnClickListener(this@SettingActivity)
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.cs_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val buttonCall = dialog.findViewById<View>(R.id.btn_cs_call)
        val buttonClose = dialog.findViewById<View>(R.id.btn_cs_close)

        buttonCall.setOnClickListener {
            Toast.makeText(this, "Menghubungi Customer Service", Toast.LENGTH_SHORT).show()
            val phoneNumber = "+62 812 3456 7890"
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber"))
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        }
        buttonClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
