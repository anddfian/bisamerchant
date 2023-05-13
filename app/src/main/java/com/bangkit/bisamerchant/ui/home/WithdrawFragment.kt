package com.bangkit.bisamerchant.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.bisamerchant.databinding.FragmentWithdrawBinding
import com.bangkit.bisamerchant.ui.pin.PinActivity

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