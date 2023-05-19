package com.bangkit.bisamerchant.ui.pin

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityPinBinding
import com.bangkit.bisamerchant.services.Merchant.updateBalanceMerchant
import com.bangkit.bisamerchant.services.Owner


class PinActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityPinBinding? = null
    private val binding get() = _binding!!
    private var times = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityPinBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initTopAppBar()
        initClickListener()
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

    private fun initClickListener() {
        binding.btnEnter.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_enter -> {
                val digit1 = binding.digit1.text
                val digit2 = binding.digit2.text
                val digit3 = binding.digit3.text
                val digit4 = binding.digit4.text
                val digit5 = binding.digit5.text
                val digit6 = binding.digit6.text
                if (digit1.isEmpty() || digit2.isEmpty() || digit3.isEmpty() || digit4.isEmpty() || digit5.isEmpty() || digit6.isEmpty()) {
                    Toast.makeText(this, "PIN is required!", Toast.LENGTH_SHORT).show()
                } else {
                    Owner.getPinOwner(
                        onSuccess = { decryptPin ->
                            val pin = "$digit1$digit2$digit3$digit4$digit5$digit6"
                            if (pin == decryptPin) {
                                val amount = intent.getStringExtra("amount")
                                val bankAccountNo = intent.getStringExtra("bankAccountNo")
                                val bankInst = intent.getStringExtra("bankInst")
                                if (bankInst != null) {
                                    updateBalanceMerchant(this, this@PinActivity, amount?.toLong() ?: 0L, bankAccountNo?.toLong() ?: 0L, bankInst)
                                }
                            } else {
                                Toast.makeText(this, "Wrong PIN!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onFailure = { exception ->
                            Toast.makeText(this@PinActivity, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}