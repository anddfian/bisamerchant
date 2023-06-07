package com.bangkit.bisamerchant

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.merchantsetting.model.Merchant
import com.bangkit.bisamerchant.domain.merchantsetting.usecase.GetMerchantActive
import com.bangkit.bisamerchant.domain.merchantsetting.usecase.UpdateMerchantInfo
import com.bangkit.bisamerchant.presentation.merchantsetting.viewmodel.MerchantSettingViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MerchantSettingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var viewModel: MerchantSettingViewModel
    private lateinit var getMerchantActive: GetMerchantActive
    private lateinit var updateMerchantInfo: UpdateMerchantInfo
    private lateinit var merchantObserver: Observer<Merchant>
    private lateinit var messageObserver: Observer<String>
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getMerchantActive = mockk(relaxed = true)
        updateMerchantInfo = mockk(relaxed = true)
        merchantObserver = mockk(relaxed = true)
        messageObserver = mockk(relaxed = true)
        isLoadingObserver = mockk(relaxed = true)
        viewModel = MerchantSettingViewModel(getMerchantActive, updateMerchantInfo)
        viewModel.merchant.observeForever(merchantObserver)
        viewModel.message.observeForever(messageObserver)
        viewModel.isLoading.observeForever(isLoadingObserver)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
        viewModel.merchant.removeObserver(merchantObserver)
        viewModel.message.removeObserver(messageObserver)
        viewModel.isLoading.removeObserver(isLoadingObserver)
    }

    @Test
    fun getMerchantActive() =
        testScope.runBlockingTest {
            // Given
            val mockMerchant = mockk<Merchant>()

            coEvery { getMerchantActive.execute(any()) } answers {
                val merchantCallback = arg<(Merchant) -> Unit>(0)
                merchantCallback.invoke(mockMerchant)
                mockk(relaxed = true)
            }

            // When
            viewModel.getMerchantActive()

            // Then
            verify { merchantObserver.onChanged(mockMerchant) }

            confirmVerified(merchantObserver)
        }

    @Test
    fun updateMerchantInfo() =
        testScope.runBlockingTest {
            // Given
            val name = "Merchant Name"
            val address = "Merchant Address"
            val type = "Merchant Type"
            val newPhoto: Uri? = null
            val successMessage = "Merchant info updated successfully"

            coEvery {
                updateMerchantInfo.execute(name, address, type, newPhoto)
            } returns flowOf(successMessage)

            // When
            viewModel.updateMerchantInfo(name, address, type, newPhoto)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { messageObserver.onChanged(successMessage) }

            confirmVerified(isLoadingObserver, messageObserver)
        }
}
