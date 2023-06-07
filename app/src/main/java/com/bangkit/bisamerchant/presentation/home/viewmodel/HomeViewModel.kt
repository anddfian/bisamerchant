package com.bangkit.bisamerchant.presentation.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.home.model.DetailTransaction
import com.bangkit.bisamerchant.domain.home.model.Merchant
import com.bangkit.bisamerchant.domain.home.model.Transaction
import com.bangkit.bisamerchant.domain.home.usecase.DeleteMerchant
import com.bangkit.bisamerchant.domain.home.usecase.GetHideAmount
import com.bangkit.bisamerchant.domain.home.usecase.GetMerchantActive
import com.bangkit.bisamerchant.domain.home.usecase.GetMerchantId
import com.bangkit.bisamerchant.domain.home.usecase.GetMerchants
import com.bangkit.bisamerchant.domain.home.usecase.GetTotalAmountTransactions
import com.bangkit.bisamerchant.domain.home.usecase.GetTransactionsToday
import com.bangkit.bisamerchant.domain.home.usecase.PostTransaction
import com.bangkit.bisamerchant.domain.home.usecase.UpdateHideAmount
import com.bangkit.bisamerchant.domain.home.usecase.UpdateMerchantStatus
import com.bangkit.bisamerchant.domain.home.usecase.ValidateWithdrawAmount
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deleteMerchant: DeleteMerchant,
    private val getHideAmount: GetHideAmount,
    private val getMerchantActive: GetMerchantActive,
    private val getMerchantId: GetMerchantId,
    private val getMerchants: GetMerchants,
    private val getTotalAmountTransactions: GetTotalAmountTransactions,
    private val getTransactionsToday: GetTransactionsToday,
    private val postTransaction: PostTransaction,
    private val updateHideAmount: UpdateHideAmount,
    private val updateMerchantStatus: UpdateMerchantStatus,
    private val validateWithdrawAmount: ValidateWithdrawAmount,
) : ViewModel() {
    private val _merchant = MutableLiveData<Merchant>()
    val merchant: LiveData<Merchant> get() = _merchant

    private val _merchantsList = MutableLiveData<List<Merchant>>()
    val merchantsList: LiveData<List<Merchant>> get() = _merchantsList

    private val _isAmountHide = MutableLiveData<Boolean>()
    val isAmountHide: LiveData<Boolean> get() = _isAmountHide

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isAmountValidated = MutableLiveData<Boolean>()
    val isAmountValidated: LiveData<Boolean> get() = _isAmountValidated

    private val _totalAmountTransactionToday = MutableLiveData<Long>()
    val totalAmountTransactionToday: LiveData<Long> get() = _totalAmountTransactionToday

    private var listenerRegistration: ListenerRegistration? = null

    fun getTransactionsToday() {
        viewModelScope.launch {
            listenerRegistration = getTransactionsToday.execute { transactions ->
                _transactions.value = transactions
                _totalAmountTransactionToday.value =
                    getTotalAmountTransactions.execute(transactions)
            }
        }
    }

    fun postTransaction(
        detailTransaction: DetailTransaction
    ) {
        viewModelScope.launch {
            postTransaction.execute(detailTransaction)
                .onStart {
                    _isLoading.value = true
                }
                .catch { e ->
                    _message.value = "Terjadi kesalahan: ${e.message}"
                    _isLoading.value = false
                }
                .collect { result ->
                    _isLoading.value = false
                    _message.value = result
                }
        }
    }

    fun validateWithdrawAmount(
        amount: Long,
    ) {
        viewModelScope.launch {
            validateWithdrawAmount.execute(amount)
                .onStart {
                    _isLoading.value = true
                }
                .catch { e ->
                    _message.value = "Terjadi kesalahan: ${e.message}"
                    _isLoading.value = false
                }
                .collect { result ->
                    _isLoading.value = false
                    _message.value = result
                    if (isAmountValidated.value != true && message.value == AMOUNT_VALIDATED) {
                        _isAmountValidated.value = true
                    }
                    if (isAmountValidated.value == true && message.value != AMOUNT_VALIDATED) {
                        _isAmountValidated.value = false
                    }
                }
        }
    }

    fun getMerchantActive() {
        viewModelScope.launch {
            listenerRegistration = getMerchantActive.execute { merchant ->
                _merchant.value = merchant
            }
        }
    }

    fun getMerchants() {
        viewModelScope.launch {
            listenerRegistration = getMerchants.execute { merchants ->
                _merchantsList.value = merchants
            }
        }
    }

    fun updateMerchantStatus(id: String?) {
        viewModelScope.launch {
            updateMerchantStatus.execute(id)
        }
    }

    fun deleteMerchant() {
        viewModelScope.launch {
            deleteMerchant.execute()
        }
    }

    fun getMerchantId() =
        runBlocking {
            getMerchantId.execute()
        }

    fun getHideAmount() {
        viewModelScope.launch {
            _isAmountHide.value = getHideAmount.execute()
        }
    }

    fun updateHideAmount(hide: Boolean) {
        viewModelScope.launch {
            updateHideAmount.execute(hide)
        }
    }

    companion object {
        private const val AMOUNT_VALIDATED = "Silakan masukkan pin"
    }

}