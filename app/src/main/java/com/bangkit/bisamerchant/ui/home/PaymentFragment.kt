package com.bangkit.bisamerchant.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        _binding?.paymentContainer?.minimumHeight = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}