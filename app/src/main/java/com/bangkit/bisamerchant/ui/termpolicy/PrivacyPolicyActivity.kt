package com.bangkit.bisamerchant.ui.termpolicy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.bangkit.bisamerchant.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    private var _binding: ActivityPrivacyPolicyBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindWebData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun bindWebData() {
        binding.wvTerm.webViewClient = WebViewClient()
        binding.wvTerm.loadUrl("https://docs.google.com/document/d/e/2PACX-1vSgrgOxHMEn47NYeVPKFe41bTnurFba7NmEddOqjLsamlPA2MY-DnlPggUw5Dht6PzH89eIz3NtSaU8/pub")
    }
}