package com.bangkit.bisamerchant.presentation.termpolicy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebViewClient
import com.bangkit.bisamerchant.databinding.ActivityPrivacyPolicyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacyPolicyActivity : AppCompatActivity() {
    private var _binding: ActivityPrivacyPolicyBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
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
            title = "Kebijakan Privasi"
        }
    }

    private fun bindWebData() {
        binding.wvTerm.webViewClient = WebViewClient()
        binding.wvTerm.loadUrl("https://docs.google.com/document/d/e/2PACX-1vSgrgOxHMEn47NYeVPKFe41bTnurFba7NmEddOqjLsamlPA2MY-DnlPggUw5Dht6PzH89eIz3NtSaU8/pub")
    }
}