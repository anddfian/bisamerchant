package com.bangkit.bisamerchant.ui.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.data.response.Merchant
import com.bangkit.bisamerchant.data.response.Transaction
import com.bangkit.bisamerchant.databinding.MerchantAccountCardBinding
import com.bangkit.bisamerchant.helper.Utils
import com.bangkit.bisamerchant.ui.invoice.DetailTransactionActivity
import com.bangkit.bisamerchant.ui.splashscreen.SplashScreenActivity
import com.bumptech.glide.Glide

class MerchantAccountAdapter(
    private val merchantViewModel: MerchantViewModel,
    private val transactionViewModel: TransactionViewModel,
    private val listMerchant: ArrayList<Merchant>
) :
    RecyclerView.Adapter<MerchantAccountAdapter.ViewHolder>() {

    private var _binding: MerchantAccountCardBinding? = null
    private val binding get() = _binding!!

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
                merchantViewModel.observeMerchantActive()
                merchantViewModel.changeMerchantStatus(merchant.id)
                val intent = Intent(holder.itemView.context, HomeActivity::class.java)
                ContextCompat.startActivity(holder.itemView.context, intent, null)
                (holder.itemView.context as? Activity)?.finish()

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