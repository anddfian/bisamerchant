package com.bangkit.bisamerchant.ui.home.transaction

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.core.data.model.Transaction
import com.bangkit.bisamerchant.core.helper.Utils
import com.bangkit.bisamerchant.databinding.TransactionCardBinding
import com.bangkit.bisamerchant.ui.invoice.DetailTransactionActivity

class TransactionAdapter(private val listTransaction: ArrayList<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var _binding: TransactionCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            TransactionCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount() = listTransaction.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = listTransaction[position]

        holder.apply {
            transactionCard.setOnClickListener {
                val intent = Intent(holder.itemView.context, DetailTransactionActivity::class.java)
                intent.putExtra(DetailTransactionActivity.EXTRA_ID, transaction.id)
                ContextCompat.startActivity(holder.itemView.context, intent, null)
            }
            if (transaction.trxType == "PAYMENT") {
                ivTransactionImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.ivTransactionImage.context, R.drawable.ic_arrow_bottom_left_24
                    )
                )
                tvAmount.text = holder.tvAmount.context.getString(
                    R.string.rp, Utils.currencyFormat(transaction.amount)
                )
                tvAmount.setTextColor(
                    ContextCompat.getColor(
                        holder.tvAmount.context, R.color.Success
                    )
                )
                tvTransactionTitle.text = holder.tvTransactionTitle.context.getString(
                    R.string.cash_in
                )
            } else {
                ivTransactionImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.ivTransactionImage.context, R.drawable.ic_arrow_top_right_24
                    )
                )
                tvAmount.setTextColor(
                    ContextCompat.getColor(
                        holder.tvAmount.context, R.color.md_theme_light_error
                    )
                )
                tvTransactionTitle.text = holder.tvTransactionTitle.context.getString(
                    R.string.cash_out
                )
                tvAmount.text =
                    holder.tvAmount.context.getString(
                        R.string.minus_rp,
                        Utils.currencyFormat(transaction.amount)
                    )
            }
            tvTransactionSupport.text =
                transaction.timestamp?.let { Utils.simpleDateFormat(it, "HH:mm") }
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivTransactionImage: ImageView = binding.ivTransactionImg
        val tvTransactionTitle: TextView = binding.tvTransactionTitle
        val tvAmount: TextView = binding.tvAmount
        val tvTransactionSupport: TextView = binding.tvTransactionSupport
        val transactionCard: CardView = binding.transactionCard
    }
}