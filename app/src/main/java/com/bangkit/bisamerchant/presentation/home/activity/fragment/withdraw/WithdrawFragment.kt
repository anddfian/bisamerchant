package com.bangkit.bisamerchant.presentation.home.activity.fragment.withdraw

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.FragmentWithdrawBinding
import com.bangkit.bisamerchant.databinding.TransactionBottomSheetBinding
import com.bangkit.bisamerchant.domain.home.model.DetailTransaction
import com.bangkit.bisamerchant.presentation.home.viewmodel.HomeViewModel
import com.bangkit.bisamerchant.presentation.pin.activity.PinActivity
import com.bangkit.bisamerchant.presentation.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithdrawFragment : Fragment() {

    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private var _bottomSheetDialog: BottomSheetDialog? = null
    private val bottomSheetDialog get() = _bottomSheetDialog!!

    private var _transactionBottomSheetBinding: TransactionBottomSheetBinding? = null
    private val transactionBottomSheetBinding get() = _transactionBottomSheetBinding!!

    private var balance: Long? = 0L
    private var withdrawAmount = 0L
    private var withdrawBankInst = ""
    private var withdrawAccountNumber = 0L

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
        setupFilterBottomSheet()
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
        homeViewModel.getMerchantActive()
        homeViewModel.merchant.observe(viewLifecycleOwner) { merchant ->
            balance = merchant.balance
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        homeViewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFilterBottomSheet() {
        _transactionBottomSheetBinding =
            TransactionBottomSheetBinding.inflate(layoutInflater)
        _bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(transactionBottomSheetBinding.root)
    }

    private val pinActivityLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val isPinValid = result.data?.getBooleanExtra("EXTRA_VALIDATION", false)
            val merchantId = homeViewModel.getMerchantId()

            if (isPinValid == true) {
                homeViewModel.postTransaction(
                    DetailTransaction(
                        amount = withdrawAmount,
                        bankAccountNo = withdrawAccountNumber,
                        bankInst = withdrawBankInst,
                        merchantId = merchantId,
                        timestamp = System.currentTimeMillis(),
                        trxType = "MERCHANT_WITHDRAW"
                    )
                )
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
                Toast.makeText(
                    requireContext(), "Amount must more than equal to 10000!", Toast.LENGTH_SHORT
                ).show()
            } else if (bank.isEmpty()) {
                Toast.makeText(requireContext(), "Bank Name is required!", Toast.LENGTH_SHORT)
                    .show()
            } else if (number.isEmpty()) {
                Toast.makeText(requireContext(), "Account Number is required!", Toast.LENGTH_SHORT)
                    .show()
            } else if (number.length < 9) {
                Toast.makeText(
                    requireContext(), "Account Number must more than 8!", Toast.LENGTH_SHORT
                ).show()
            } else {
                balance.let {
                    if (it != null) {
                        if (it < amount.toLong()) {
                            Toast.makeText(
                                requireContext(),
                                "Balance not enough",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            transactionConfirmation(amount.toLong(), bank, number.toLong())
                        }
                    }
                }
            }
        }
    }

    private fun transactionConfirmation(
        amount: Long,
        bank: String,
        number: Long,
    ) {
        bottomSheetDialog.show()

        transactionBottomSheetBinding.tvTransactionConfirmation.text =
            getString(R.string.withdraw_confirmation)

        transactionBottomSheetBinding.tvNumber.text = number.toString()

        transactionBottomSheetBinding.subtotal.text =
            getString(R.string.rp, Utils.currencyFormat(amount))

        transactionBottomSheetBinding.tvTransactionType.text =
            getString(R.string.withdraw_to, bank)

        transactionBottomSheetBinding.btnContinueTransaction.text = getString(R.string.continue_withdraw)

        transactionBottomSheetBinding.ivTransactionImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_top_right_24))

        homeViewModel.getTransactionFee(amount)
        homeViewModel.fee.observe(viewLifecycleOwner) { fee ->
            transactionBottomSheetBinding.tvAmountTransaction.text =
                getString(R.string.rp, Utils.currencyFormat(amount + fee))
            transactionBottomSheetBinding.tvFee.text =
                getString(R.string.rp, Utils.currencyFormat(fee))
        }

        transactionBottomSheetBinding.btnContinueTransaction.setOnClickListener {
            homeViewModel.validateWithdrawAmount(amount)
            homeViewModel.isAmountValidated.observe(viewLifecycleOwner) { isValidated ->
                if (isValidated == true) {
                    withdrawAmount = amount
                    withdrawBankInst = bank
                    withdrawAccountNumber = number
                    executePinLauncher()
                }
            }
            bottomSheetDialog.dismiss()
        }
    }

    private fun executePinLauncher(message: String? = null) {
        val intent = Intent(activity, PinActivity::class.java)
        intent.putExtra("EXTRA_MESSAGE", message)
        pinActivityLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}