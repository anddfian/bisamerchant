package com.bangkit.bisamerchant.presentation.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.home.model.DetailTransaction
import com.bangkit.bisamerchant.domain.home.model.Merchant
import com.bangkit.bisamerchant.domain.home.model.Transaction
import com.bangkit.bisamerchant.domain.home.usecase.GetHideAmount
import com.bangkit.bisamerchant.domain.home.usecase.GetMerchantActive
import com.bangkit.bisamerchant.domain.home.usecase.GetMerchantId
import com.bangkit.bisamerchant.domain.home.usecase.GetMerchants
import com.bangkit.bisamerchant.domain.home.usecase.GetTotalAmountTransactions
import com.bangkit.bisamerchant.domain.home.usecase.GetTransactionFee
import com.bangkit.bisamerchant.domain.home.usecase.GetTransactionsToday
import com.bangkit.bisamerchant.domain.home.usecase.PostTransaction
import com.bangkit.bisamerchant.domain.home.usecase.UpdateHideAmount
import com.bangkit.bisamerchant.domain.home.usecase.UpdateMerchantStatus
import com.bangkit.bisamerchant.domain.home.usecase.ValidateWithdrawAmount
import com.google.firebase.firestore.ListenerRegistration
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: HomeViewModel

    @Mock
    private lateinit var getHideAmount: GetHideAmount

    @Mock
    private lateinit var getMerchantActive: GetMerchantActive

    @Mock
    private lateinit var getMerchantId: GetMerchantId

    @Mock
    private lateinit var getMerchants: GetMerchants

    @Mock
    private lateinit var getTotalAmountTransactions: GetTotalAmountTransactions

    @Mock
    private lateinit var getTransactionsToday: GetTransactionsToday

    @Mock
    private lateinit var postTransaction: PostTransaction

    @Mock
    private lateinit var updateHideAmount: UpdateHideAmount

    @Mock
    private lateinit var updateMerchantStatus: UpdateMerchantStatus

    @Mock
    private lateinit var validateWithdrawAmount: ValidateWithdrawAmount

    @Mock
    private lateinit var getTransactionFee: GetTransactionFee


    private lateinit var merchantObserver: Observer<Merchant>

    @Before
    fun setUp() {
        viewModel =
            HomeViewModel(
                getHideAmount,
                getMerchantActive,
                getMerchantId,
                getMerchants,
                getTotalAmountTransactions,
                getTransactionsToday,
                postTransaction,
                updateHideAmount,
                updateMerchantStatus,
                validateWithdrawAmount,
                getTransactionFee,
            )

        merchantObserver = mockk(relaxed = true)
        viewModel.merchant.observeForever(merchantObserver)

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `postTransaction should update isLoading, message, and isLoading values correctly`() {

        val expectedResult = "Success"
        val detailTransaction = DetailTransaction(
            5000,
            "xRu9qkMRnkJ7KolpwNPl",
            "FgAsW4dvGvWdbTRVmcmWRwbdsLG3",
            null,
            null,
            "PAYMENT",
            "1hlKE2b2Jh1ku8sKwFDM",
            1685773639793
        )
        runTest {
            whenever(postTransaction.execute(any())).thenReturn(flow { emit(expectedResult) })
        }

        viewModel.postTransaction(detailTransaction)

        assertEquals(viewModel.message.value, expectedResult)
    }

    @Test
    fun `validateWithdrawAmount should update isLoading, message, and isAmountValidated values correctly`() {

        val expectedResult = "Success"

        runTest {
            whenever(validateWithdrawAmount.execute(any())).thenReturn(flow { emit(expectedResult) })
        }

        viewModel.validateWithdrawAmount(100)

        assertEquals(viewModel.message.value, expectedResult)
    }

    @Test
    fun `getTransactionFee should update isLoading and fee values correctly`() {

        val amount = 1000
        val expectedResult = 7

        runTest {
            whenever(getTransactionFee.execute(amount.toLong())).thenReturn(flow {
                emit(
                    expectedResult.toLong()
                )
            })
        }

        viewModel.getTransactionFee(amount.toLong())

        assertEquals(viewModel.fee.value, expectedResult.toLong())
    }

    @Test
    fun `getMerchants should update isLoading and merchantsList values correctly`() {

        val expectedList = listOf<Merchant>()

        runTest {
            whenever(getMerchants.execute()).thenReturn(flow { emit(expectedList) })
        }

        viewModel.getMerchants()

        assertEquals(viewModel.merchantsList.value, expectedList)
    }

    @Test
    fun `updateMerchantStatus should execute correctly`() {

        val id = "merchant_id"

        viewModel.updateMerchantStatus(id)

        runBlocking {
            verify(updateMerchantStatus).execute(id)
        }
    }

    @Test
    fun `getMerchantId should return the correct result`() {
        val expectedResult = "merchant_id"

        runBlocking {
            whenever(getMerchantId.execute()).thenReturn(expectedResult)
        }

        val result = viewModel.getMerchantId()

        assertEquals(expectedResult, result)
    }

    @Test
    fun `getHideAmount should update isAmountHide value correctly`() {
        val expectedResult = true

        runBlocking {
            whenever(getHideAmount.execute()).thenReturn(expectedResult)
        }

        viewModel.getHideAmount()

        assertTrue(viewModel.isAmountHide.value == expectedResult)
    }

    @Test
    fun `updateHideAmount should execute correctly`() {
        val hide = true

        viewModel.updateHideAmount(hide)

        runBlocking {
            verify(updateHideAmount).execute(hide)
        }
    }

    @Test
    fun `getTransactionsToday should update transactions and totalAmountTransactionToday correctly`() {
        val transactions = listOf<Transaction>()
        val totalAmountTransactionToday = 999L

        runBlocking {
            whenever(getTransactionsToday.execute(any())).thenAnswer { invocation ->
                val callback = invocation.arguments[0] as (List<Transaction>) -> Unit
                callback(transactions)
                mock<ListenerRegistration>()
            }
        }

        runBlocking {
            whenever(getTotalAmountTransactions.execute(transactions)).thenReturn(
                totalAmountTransactionToday
            )
        }

        viewModel.getTransactionsToday()

        assertEquals(transactions, viewModel.transactions.value)
        assertEquals(totalAmountTransactionToday, viewModel.totalAmountTransactionToday.value)
    }

    @Test
    fun `getMerchantActive should update merchant correctly`() {

        val merchant = Merchant()

        runBlocking {
            whenever(getMerchantActive.execute(any())).thenAnswer { invocation ->
                val callback = invocation.arguments[0] as (Merchant) -> Unit
                callback(merchant)
                mock<ListenerRegistration>()
            }
        }

        viewModel.getMerchantActive()

        assertEquals(merchant, viewModel.merchant.value)
    }

}