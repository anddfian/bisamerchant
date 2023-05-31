package com.bangkit.bisamerchant.presentation.termpolicy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebViewClient
import com.bangkit.bisamerchant.databinding.ActivityTermConditionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermConditionActivity : AppCompatActivity() {
    private var _binding: ActivityTermConditionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTermConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTopAppBar()
        bindWebData()
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
            title = "Syarat dan Ketentuan"
        }
    }

    private fun bindWebData() {
        binding.wvTerm.webViewClient = WebViewClient()
        binding.wvTerm.loadUrl("https://docs.google.com/document/d/e/2PACX-1vSiQxl_x29Kh3fK9MS3hQn5pUNBwmoN4cPBWuW0w5wd8EYyHOE7gHRfICV-Its13YPCaivToi4q6WIK/pub")
    }
}