package com.bangkit.bisamerchant.presentation.pin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityPinBinding
import com.bangkit.bisamerchant.core.services.Merchant.updateBalanceMerchant
import com.bangkit.bisamerchant.core.services.Owner
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PinActivity : AppCompatActivity(), TextWatcher {
    private var _binding: ActivityPinBinding? = null
    private val binding get() = _binding!!
    private val editTextArray: ArrayList<EditText> = ArrayList(NUM_OF_DIGITS)
    private var numTemp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityPinBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initTopAppBar()

        val layout: LinearLayout = findViewById(R.id.codeLayout)
        for (index in 0 until (layout.childCount)) {
            val view: View = layout.getChildAt(index)
            if (view is EditText) {
                editTextArray.add(index, view)
                editTextArray[index].addTextChangedListener(this)
            }
        }

        editTextArray[0].requestFocus() //After the views are initialized we focus on the first view
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
            title = "PIN Verification"
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        numTemp = s.toString()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val pinDigits = arrayOf(
            binding.digit1.text,
            binding.digit2.text,
            binding.digit3.text,
            binding.digit4.text,
            binding.digit5.text,
            binding.digit6.text
        )

        val pinEntered = StringBuilder()
        for (i in 0 until editTextArray.size) {
            if (s === editTextArray[i].editableText) {
                if (s.isNullOrBlank()) {
                    return
                }
                if (s.length >= 2) {
                    val newTemp = s.toString().substring(s.length - 1, s.length)
                    if (newTemp != numTemp) {
                        editTextArray[i].setText(newTemp)
                    } else {
                        editTextArray[i].setText(s.toString().substring(0, s.length - 1))
                    }
                } else if (i != editTextArray.size - 1) {
                    editTextArray[i + 1].requestFocus()
                    editTextArray[i + 1].setSelection(editTextArray[i + 1].length())
                    return
                }
                else if (i == editTextArray.size - 1) {
                    editTextArray[i].clearFocus()
                    editTextArray[0].requestFocus()
                }
            }
            pinEntered.append(pinDigits[i])
        }

        if (pinEntered.isNotEmpty()) {
            val pin = pinEntered.toString()
            val hasPinDigits = pinDigits.any { it.isNotEmpty() }
            if (hasPinDigits) {
                Owner.getPinOwner(onSuccess = { decryptPin ->
                    if (pin == decryptPin) {
                        val amount = intent.getStringExtra("amount")
                        val bankAccountNo = intent.getStringExtra("bankAccountNo")
                        val bankInst = intent.getStringExtra("bankInst")
                        if (bankInst != null) {
                            updateBalanceMerchant(
                                this,
                                this@PinActivity,
                                amount?.toLong() ?: 0L,
                                bankAccountNo?.toLong() ?: 0L,
                                bankInst
                            )
                        }
                    } else {
                        Toast.makeText(this, "Wrong PIN!", Toast.LENGTH_SHORT).show()
                    }
                }, onFailure = { exception ->
                    Toast.makeText(this@PinActivity, exception.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                })
            }
        }
    }

    companion object {
        const val NUM_OF_DIGITS = 6
    }
}