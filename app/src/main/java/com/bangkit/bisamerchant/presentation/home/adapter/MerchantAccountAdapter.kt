package com.bangkit.bisamerchant.presentation.home.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bisamerchant.domain.home.model.Merchant
import com.bangkit.bisamerchant.databinding.MerchantAccountCardBinding
import com.bangkit.bisamerchant.presentation.home.viewmodel.HomeViewModel
import com.bangkit.bisamerchant.presentation.splashscreen.activity.SplashScreenActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MerchantAccountAdapter(
    private val homeViewModel: HomeViewModel,
    private val listMerchant: ArrayList<Merchant>
) :
    RecyclerView.Adapter<MerchantAccountAdapter.ViewHolder>() {

    private var _binding: MerchantAccountCardBinding? = null
    private val binding get() = _binding!!

    private val lifecycleOwner by lazy {
        binding.root.context as? LifecycleOwner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            MerchantAccountCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount() = listMerchant.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val merchant = listMerchant[position]
        holder.apply {
            merchantCard.setOnClickListener {
                lifecycleOwner?.lifecycleScope?.launch {
                    if (merchant.id != homeViewModel.getMerchantId()) {
                        withContext(Dispatchers.IO) {
                            homeViewModel.updateMerchantStatus(merchant.id)
                        }

                        val intent =
                            Intent(holder.itemView.context, SplashScreenActivity::class.java)
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                        (holder.itemView.context as? Activity)?.finish()
                    }
                }
            }
            Glide.with(holder.itemView.context).load(merchant.merchantLogo)
                .into(holder.ivMerchantImage)
            tvMerchantName.text = merchant.merchantName
            tvMerchantType.text = merchant.merchantType
            rbActive.isChecked = merchant.merchantActive == true
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMerchantImage: ImageView = binding.ivMerchantLogo
        val tvMerchantName: TextView = binding.tvMerchantTitle
        val tvMerchantType: TextView = binding.tvMerchantSupport
        val rbActive: RadioButton = binding.rbActive
        val merchantCard: CardView = binding.merchantCard
    }
}