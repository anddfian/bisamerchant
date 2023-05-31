package com.bangkit.bisamerchant.presentation.profile.activity.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.FragmentLoyaltyBinding

class LoyaltyFragment : Fragment() {

    private var _binding: FragmentLoyaltyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoyaltyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt(ARG_POSITION)

        binding.apply {
            if (position == 0) {
                this.tvMerchantBenefits.text = getString(R.string.bronze_merchant_benefits)
                this.beneifts1.text = getString(R.string.bronze_benefits_1)
                this.beneifts2.text = getString(R.string.bronze_benefits_2)
            }

            if (position == 1) {
                this.tvMerchantBenefits.text = getString(R.string.silver_merchant_benefits)
                this.beneifts1.text = getString(R.string.silver_benefits_1)
                this.beneifts2.text = getString(R.string.silver_benefits_2)
            }

            if (position == 2) {
                this.tvMerchantBenefits.text = getString(R.string.gold_merchant_benefits)
                this.beneifts1.text = getString(R.string.gold_benefits_1)
                this.beneifts2.text = getString(R.string.gold_benefits_2)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var ARG_POSITION = "arg_position"
    }
}