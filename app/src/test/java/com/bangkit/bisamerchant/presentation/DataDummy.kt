package com.bangkit.bisamerchant.presentation

import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.bangkit.bisamerchant.domain.invoice.model.DetailTransaction

object DataDummy {
    fun generateDetailTransaction() = DetailTransaction(
        5000,
        "xRu9qkMRnkJ7KolpwNPl",
        "FgAsW4dvGvWdbTRVmcmWRwbdsLG3",
        null,
        null,
        "PAYMENT",
        "1hlKE2b2Jh1ku8sKwFDM",
        1685773639793
    )

    fun generateTransactionsResponse() = mutableListOf(
        Transaction(
            amount = 503500,
            trxType = "MERCHANT_WITHDRAW",
            id = "PIAk3yXgataB1BTfn3FK",
            timestamp = 1686213874107
        ),
        Transaction(
            amount = 290100,
            trxType = "PAYMENT",
            id = "WzHdxDZl4eVzbiepXR7m",
            timestamp = 1686152495574
        ),
        Transaction(
            amount = 503500,
            trxType = "MERCHANT_WITHDRAW",
            id = "2F xGtFTz3m2tow29goe8",
            timestamp = 1686152448444
        ),
        Transaction(
            amount = 1510500,
            trxType = "MERCHANT_WITHDRAW",
            id = "kIRUdXoz1IqcldrpZhw1",
            timestamp = 1686152395344
        ),
        Transaction(
            amount = 50000,
            trxType = "PAYMENT",
            id = "KutUoYkYOGJ1RmbjXqAR",
            timestamp = 1685952019009
        ),
        Transaction(
            amount = 50000,
            trxType = "PAYMENT",
            id = "bbbGGfl8X3tD8OFP3FLn",
            timestamp = 1685951998843
        ),
        Transaction(
            amount = 55000,
            trxType = "PAYMENT",
            id = "Ce6jWndmgFybVtH2t31I",
            timestamp = 1685866173829
        ),
        Transaction(
            amount = 5000,
            trxType = "PAYMENT",
            id = "i0hU02vZIBRLMnkPSpev",
            timestamp = 1685771174659
        )
    )

    fun generateFilteredTransactionsResponse() = mutableListOf(
        Transaction(
            amount = 503500,
            trxType = "MERCHANT_WITHDRAW",
            id = "PIAk3yXgataB1BTfn3FK",
            timestamp = 1686213874107
        ),
        Transaction(
            amount = 503500,
            trxType = "MERCHANT_WITHDRAW",
            id = "2F xGtFTz3m2tow29goe8",
            timestamp = 1686152448444
        ),
        Transaction(
            amount = 1510500,
            trxType = "MERCHANT_WITHDRAW",
            id = "kIRUdXoz1IqcldrpZhw1",
            timestamp = 1686152395344
        ),
    )

    fun generateTransactionsTodayResponse() = listOf(
        com.bangkit.bisamerchant.domain.home.model.Transaction(
            amount = 290100,
            trxType = "PAYMENT",
            id = "WzHdxDZl4eVzbiepXR7m",
            timestamp = 1686152495574
        ),
        com.bangkit.bisamerchant.domain.home.model.Transaction(
            amount = 50000,
            trxType = "PAYMENT",
            id = "KutUoYkYOGJ1RmbjXqAR",
            timestamp = 1685952019009
        ),
        com.bangkit.bisamerchant.domain.home.model.Transaction(
            amount = 50000,
            trxType = "PAYMENT",
            id = "bbbGGfl8X3tD8OFP3FLn",
            timestamp = 1685951998843
        ),
        com.bangkit.bisamerchant.domain.home.model.Transaction(
            amount = 55000,
            trxType = "PAYMENT",
            id = "Ce6jWndmgFybVtH2t31I",
            timestamp = 1685866173829
        ),
        com.bangkit.bisamerchant.domain.home.model.Transaction(
            amount = 5000,
            trxType = "PAYMENT",
            id = "i0hU02vZIBRLMnkPSpev",
            timestamp = 1685771174659
        )
    )
}