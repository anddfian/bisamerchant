package com.bangkit.bisamerchant.presentation.invoice.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bangkit.bisamerchant.domain.invoice.usecase.GetTransaction
import com.bangkit.bisamerchant.presentation.DataDummy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DetailTransactionViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: DetailTransactionViewModel

    @Mock
    private lateinit var getTransaction: GetTransaction

    @Before
    fun setup() {
        viewModel = DetailTransactionViewModel(getTransaction)

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getTransactionById should return correct transaction info`() {

        // Given
        val detailTransaction = DataDummy.generateDetailTransaction()
        val id = "transaction_id"

        runTest {
            // When
            `when`(getTransaction.execute(id)).thenReturn(flow { emit(detailTransaction) })
            viewModel.getTransactionById(id)

            // Then
            assertNotNull(viewModel.transaction.value)
            assertSame(viewModel.transaction.value, detailTransaction)
        }
    }

}
