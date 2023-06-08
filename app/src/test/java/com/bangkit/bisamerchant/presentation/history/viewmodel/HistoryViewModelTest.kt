package com.bangkit.bisamerchant.presentation.history.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bangkit.bisamerchant.domain.history.model.FilteredTransaction
import com.bangkit.bisamerchant.domain.history.usecase.GetFilteredTransactions
import com.bangkit.bisamerchant.domain.history.usecase.GetTotalAmountTransactions
import com.bangkit.bisamerchant.domain.history.usecase.GetTransactions
import com.bangkit.bisamerchant.presentation.DataDummy
import com.google.firebase.firestore.Query.Direction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HistoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: HistoryViewModel

    @Mock
    private lateinit var getTransactions: GetTransactions

    @Mock
    private lateinit var getTransactionsWithFilter: GetFilteredTransactions

    @Mock
    private lateinit var getTotalAmountTransactions: GetTotalAmountTransactions

    @Before
    fun setUp() {
        viewModel =
            HistoryViewModel(getTransactionsWithFilter, getTotalAmountTransactions, getTransactions)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getTransactions should return data transactions`() {
        val dummyTransactions = DataDummy.generateTransactionsResponse()
        runTest {
            `when`(getTransactions.execute()).thenReturn(flow { emit(dummyTransactions) })

            viewModel.getTransactions()

            assertNotNull(viewModel.transactions.value)
            assertSame(viewModel.transactions.value, dummyTransactions)
        }
    }

    @Test
    fun `getTransactionsWithFilter should return filtered data transactions`() {
        val dummyFilteredTransactions = DataDummy.generateFilteredTransactionsResponse()

        val dummyFilter = FilteredTransaction(
            queryDirection = Direction.DESCENDING,
            startDate = 0,
            endDate = 99999999999L,
            trxType = "MERCHANT_WITHDRAW",
        )

        runTest {
            `when`(getTransactionsWithFilter.execute(dummyFilter)).thenReturn(flow { emit(dummyFilteredTransactions) })

            viewModel.getTransactionsWithFilter(dummyFilter)

            assertNotNull(viewModel.transactions.value)
            assertSame(viewModel.transactions.value, dummyFilteredTransactions)
        }
    }
}