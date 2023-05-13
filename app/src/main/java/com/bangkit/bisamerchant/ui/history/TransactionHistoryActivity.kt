package com.bangkit.bisamerchant.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.bangkit.bisamerchant.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class TransactionHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        val filterButton: Button = findViewById(R.id.btn_filter)
        filterButton.setOnClickListener {
            BottomSheetDialog(this).apply {
                setContentView(R.layout.filter_bottom_sheet)
                show()
            }
        }
    }
}
