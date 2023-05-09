package com.bangkit.bisamerchant.ui.termpolicy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.bangkit.bisamerchant.databinding.ActivityTermConditionBinding

class TermConditionActivity : AppCompatActivity() {
    private var _binding: ActivityTermConditionBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTermConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindWebData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun bindWebData() {
        binding.wvTerm.webViewClient = WebViewClient()
        binding.wvTerm.loadUrl("https://docs.google.com/document/d/e/2PACX-1vSiQxl_x29Kh3fK9MS3hQn5pUNBwmoN4cPBWuW0w5wd8EYyHOE7gHRfICV-Its13YPCaivToi4q6WIK/pub")
    }
}