package com.bangkit.bisamerchant.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.bisamerchant.databinding.FragmentWithdrawBinding
import com.bangkit.bisamerchant.ui.pin.PinActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class WithdrawFragment : Fragment() {

    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        updateUI()
    }

    private fun updateUI() {
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
    }

    private fun initClickListener() {
        binding.withdrawButton.setOnClickListener {
            startActivity(Intent(activity, PinActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        _binding?.withdrawContainer?.minimumHeight = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}