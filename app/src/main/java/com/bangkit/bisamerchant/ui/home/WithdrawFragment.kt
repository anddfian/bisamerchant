package com.bangkit.bisamerchant.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import com.bangkit.bisamerchant.databinding.FragmentWithdrawBinding
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.ViewModelMerchantFactory
import com.bangkit.bisamerchant.ui.pin.PinActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data")

class WithdrawFragment : Fragment() {

    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!
    private var balance: Long? = 0L
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        val merchantViewModel = initMerchantViewModel()
        updateUI(merchantViewModel)
    }

    private fun initMerchantViewModel(): MerchantViewModel {
        val pref = MerchantPreferences.getInstance(requireContext().dataStore)
        val factory = ViewModelMerchantFactory.getInstance(pref)
        val merchantViewModel: MerchantViewModel by viewModels { factory }
        return merchantViewModel
    }

    private fun updateUI(merchantViewModel: MerchantViewModel) {
        val bankItems = arrayOf(
            "BCA - Bank Central Asia",
            "Mandiri",
            "BRI - Bank Rakyat Indonesia",
            "BNI - Bank Negara Indonesia",
            "CIMB Niaga",
            "BTN - Bank Tabungan Negara",
            "Maybank",
            "OCBC NISP",
            "Permata Bank",
            "DBS Bank",
            "Panin Bank",
            "BTPN - Bank Tabungan Pensiunan Nasional",
            "UOB Indonesia",
            "HSBC",
            "Standard Chartered Bank",
            "BII - Bank Internasional Indonesia",
            "BNP Paribas",
            "Bank Mega",
            "Bank Danamon",
            "Bank Bukopin",
            "Bank Tabungan Pemuda",
            "Bank Muamalat",
            "Bank Mestika",
            "Bank Jago",
            "Bank Ina",
            "Bank Ganesha",
            "Bank Fama Internasional",
            "Bank Ekonomi Raharja",
            "Bank Chinatrust Indonesia",
            "Bank BPD DIY",
            "Bank BJB",
            "Bank Artha Graha Internasional",
            "Bank Amar Indonesia",
            "Bank Agris",
            "Bank Syariah Mandiri",
            "Bank Kaltimtara",
        )
        (binding.edWithdrawBank as? MaterialAutoCompleteTextView)?.setSimpleItems(bankItems)
        merchantViewModel.observeMerchantActive()
        merchantViewModel.merchant.observe(viewLifecycleOwner) { merchant ->
            balance = merchant.balance
        }
    }

    private fun initClickListener() {
        binding.withdrawButton.setOnClickListener {
            val amount = binding.edWithdrawAmount.editableText.toString()
            val bank = binding.edWithdrawBank.editableText.toString()
            val number = binding.edAccountNumber.editableText.toString()
            val amountInt: Int = try {
                amount.toInt()
            } catch (e: NumberFormatException) {
                0
            }
            if (amount.isEmpty()) {
                Toast.makeText(requireContext(), "Amount is required!", Toast.LENGTH_SHORT).show()
            } else if (amountInt < 10000) {
                Toast.makeText(requireContext(), "Amount must more than equal to 10000!", Toast.LENGTH_SHORT).show()
            } else if (bank.isEmpty()) {
                Toast.makeText(requireContext(), "Bank Name is required!", Toast.LENGTH_SHORT).show()
            } else if (number.isEmpty()) {
                Toast.makeText(requireContext(), "Account Number is required!", Toast.LENGTH_SHORT).show()
            } else if (number.length < 9) {
                Toast.makeText(requireContext(), "Account Number must more than 8!", Toast.LENGTH_SHORT).show()
            } else {
                if (balance!! < amount.toLong()) {
                    Toast.makeText(requireContext(), "Balance not enough", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(activity, PinActivity::class.java)
                    intent.putExtra("amount", amount)
                    intent.putExtra("bankInst", bank)
                    intent.putExtra("bankAccountNo", number)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}