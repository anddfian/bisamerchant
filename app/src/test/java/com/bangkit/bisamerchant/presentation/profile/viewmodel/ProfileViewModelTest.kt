package com.bangkit.bisamerchant.presentation.profile.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bangkit.bisamerchant.domain.profile.model.Merchant
import com.bangkit.bisamerchant.domain.profile.usecase.GetMerchantActive
import com.bangkit.bisamerchant.domain.profile.usecase.GetTotalTransactionsFromLastMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
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


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ProfileViewModel

    @Mock
    private lateinit var getMerchantActive: GetMerchantActive

    @Mock
    private lateinit var getTotalTransactionsFromLastMonth: GetTotalTransactionsFromLastMonth

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel(getMerchantActive, getTotalTransactionsFromLastMonth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getMerchantActive should return merchant active when success`() {

        val expectedResult = Merchant(
            id = "merchantId",
            balance = 0L,
            merchantActive = true,
            merchantLogo = "https://image",
            merchantAddress = "Merchant Location",
            merchantType = "Merchant Type",
            email = "example@email.com",
            merchantName = "Merchant Name",
        )

        runTest {
            `when`(getMerchantActive.execute()).thenReturn(flow { emit(expectedResult) })

            viewModel.getMerchantActive()

            assertNotNull(viewModel.merchant.value)
            assertEquals(viewModel.merchant.value, expectedResult)
        }
    }

    @Test
    fun `getTotalTransactionsFromLastMonth should return total amount of transactions`() {
        val totalAmountExpected = 999L

        runTest {
            `when`(getTotalTransactionsFromLastMonth.execute()).thenReturn(flow { emit(999L) })

            viewModel.getTotalTransactionsFromLastMonth()

            assertNotNull(viewModel.totalAmountTransactionsFromLastMonth.value)
            assertEquals(viewModel.totalAmountTransactionsFromLastMonth.value, totalAmountExpected)
        }
    }
}