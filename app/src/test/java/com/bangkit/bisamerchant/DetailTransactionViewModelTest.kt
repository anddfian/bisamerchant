package com.bangkit.bisamerchant

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.invoice.model.DetailTransaction
import com.bangkit.bisamerchant.domain.invoice.usecase.GetTransaction
import com.bangkit.bisamerchant.presentation.invoice.viewmodel.DetailTransactionViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailTransactionViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var viewModel: DetailTransactionViewModel
    private lateinit var getTransaction: GetTransaction

    @Before
    fun setup() {
        getTransaction = mockk(relaxed = true)

        viewModel = DetailTransactionViewModel(getTransaction)

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun getTransactionById() = testScope.runBlockingTest {
        // Given
        val detailTransaction = DetailTransaction(
        5000,
        "xRu9qkMRnkJ7KolpwNPl",
        "FgAsW4dvGvWdbTRVmcmWRwbdsLG3",
        null,
        null,
        "PAYMENT",
        "1hlKE2b2Jh1ku8sKwFDM",
        1685773639793)
        val id = "transaction_id"

        coEvery { getTransaction.execute(id) } coAnswers { detailTransaction }

        val observer = mockk<Observer<DetailTransaction>>(relaxed = true)
        viewModel.transaction.observeForever(observer)

        // When
        viewModel.getTransactionById(id)

        // Then
        verify { observer.onChanged(detailTransaction) }

        coVerify { getTransaction.execute(id) }

        confirmVerified(observer, getTransaction)
    }
}
