package com.bangkit.bisamerchant.presentation.profile.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityProfileBinding
import com.bangkit.bisamerchant.presentation.profile.viewmodel.ProfileViewModel
import com.bangkit.bisamerchant.presentation.utils.Utils
import com.bumptech.glide.Glide
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
        profileViewModel.getTotalTransactionsFromLastMonth()
        profileViewModel.merchant.observe(this) { merchant ->
            binding.apply {
                tvMerchantName.text = merchant.merchantName
                tvMerchantAddress.text = merchant.merchantAddress
                tvMerchantEmail.text = merchant.email
                tvMerchantType.text = merchant.merchantType
            }
            Glide.with(binding.root)
                .load(merchant.merchantLogo)
                .placeholder(R.drawable.placeholder)
                .into(binding.ivMerchantLogo)
        }
        profileViewModel.totalAmountTransactionsFromLastMonth.observe(this) { amountFromLastMonth ->
            binding.apply {
                when (amountFromLastMonth) {
                    in 0L..5000000L -> {
                        tvMerchantLoyalty.text = getString(R.string.bronze_merchant)
                        loyaltyCount.text = resources.getString(R.string.rp, Utils.currencyFormat(amountFromLastMonth))
                        loyaltyCountToNextLevel.text = resources.getString(R.string.rp, Utils.currencyFormat(5000000L))
                        progressIndicator.progress =
                            ((amountFromLastMonth.times(100) / 5000000L)).toInt()
                        tvMerchantBenefits.text = getString(R.string.bronze_merchant_benefits)
                        beneifts1.text = getString(R.string.bronze_benefits_1)
                        beneifts2.text = getString(R.string.bronze_benefits_2)
                        info1.text = getString(R.string.bronze_info)
                    }
                    in 5000000L..170000000L -> {
                        tvMerchantLoyalty.text = getString(R.string.silver_merchant)
                        loyaltyCount.text = resources.getString(R.string.rp, Utils.currencyFormat(amountFromLastMonth))
                        loyaltyCountToNextLevel.text = resources.getString(R.string.rp, Utils.currencyFormat(170000000L))
                        progressIndicator.progress =
                            ((amountFromLastMonth.times(100) / 170000000L)).toInt()
                        loyaltyNow.text = getString(R.string.silver)
                        loyaltyNext.text = getString(R.string.gold)
                        tvMerchantBenefits.text = getString(R.string.silver_merchant_benefits)
                        beneifts1.text = getString(R.string.silver_benefits_1)
                        beneifts2.text = getString(R.string.silver_benefits_2)
                        info1.text = getString(R.string.silver_info)
                    }
                    else -> {
                        tvMerchantLoyalty.text = getString(R.string.gold_merchant)
                        loyaltyCount.text = resources.getString(R.string.rp, Utils.currencyFormat(amountFromLastMonth))
                        loyaltyCountToNextLevel.visibility = View.INVISIBLE
                        progressIndicator.progress = 100
                        loyaltyNow.text = getString(R.string.gold)
                        loyaltyNext.visibility = View.INVISIBLE
                        tvMerchantBenefits.text = getString(R.string.gold_merchant_benefits)
                        beneifts1.text = getString(R.string.gold_benefits_1)
                        beneifts2.text = getString(R.string.gold_benefits_2)
                        info1.text = getString(R.string.gold_info)
                    }
                }
            }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}