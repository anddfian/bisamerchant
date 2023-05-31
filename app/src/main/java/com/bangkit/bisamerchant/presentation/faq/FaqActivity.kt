package com.bangkit.bisamerchant.presentation.faq

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityFaqBinding
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaqActivity : AppCompatActivity() {
    private var _binding: ActivityFaqBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClickListener()
        initTopAppBar()
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

    private fun initTopAppBar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "FAQ"
        }
    }

    private fun initClickListener() {
        binding.apply {
            baseCardview.setOnClickListener {
                clickQuestionHandler(
                    this.baseCardview,
                    this.cardGroup,
                    this.show
                )
            }
            baseCardview2.setOnClickListener {
                clickQuestionHandler(
                    this.baseCardview2,
                    this.cardGroup2,
                    this.show2
                )
            }
            baseCardview3.setOnClickListener {
                clickQuestionHandler(
                    this.baseCardview3,
                    this.cardGroup3,
                    this.show3
                )
            }
            baseCardview4.setOnClickListener {
                clickQuestionHandler(
                    this.baseCardview4,
                    this.cardGroup4,
                    this.show4
                )
            }
            baseCardview5.setOnClickListener {
                clickQuestionHandler(
                    this.baseCardview5,
                    this.cardGroup5,
                    this.show5
                )
            }
        }
    }

    private fun clickQuestionHandler(
        baseCardView: MaterialCardView,
        cardGroup: Group,
        show: ImageView
    ) {
        if (cardGroup.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(
                baseCardView,
                AutoTransition()
            )
            cardGroup.visibility = View.GONE
            show.setImageResource(R.drawable.ic_filled_arrow_down_24)
        } else {
            TransitionManager.beginDelayedTransition(
                baseCardView,
                AutoTransition()
            )
            cardGroup.visibility = View.VISIBLE
            show.setImageResource(R.drawable.ic_filled_arrow_up_24)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}