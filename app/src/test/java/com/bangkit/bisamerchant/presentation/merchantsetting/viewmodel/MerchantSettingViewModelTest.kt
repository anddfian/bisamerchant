package com.bangkit.bisamerchant.presentation.merchantsetting.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.merchantsetting.model.Merchant
import com.bangkit.bisamerchant.domain.merchantsetting.usecase.GetMerchantActive
import com.bangkit.bisamerchant.domain.merchantsetting.usecase.UpdateMerchantInfo
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MerchantSettingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: MerchantSettingViewModel

    @Mock
    private lateinit var getMerchantActive: GetMerchantActive

    @Mock
    private lateinit var updateMerchantInfo: UpdateMerchantInfo

    private lateinit var merchantObserver: Observer<Merchant>
    private lateinit var messageObserver: Observer<String>
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

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

        viewModel.merchant.removeObserver(merchantObserver)
        viewModel.message.removeObserver(messageObserver)
        viewModel.isLoading.removeObserver(isLoadingObserver)
    }

    @Test
    fun `getMerchantActive should return data merchant active when success`() {

        val expectedResult = Merchant(
            merchantLogo = "https://image",
            merchantAddress = "Merchant Location",
            merchantType = "Merchant Type",
            merchantName = "Merchant Name",
        )

        runTest {
            `when`(getMerchantActive.execute()).thenReturn(flow { emit(expectedResult) })

            viewModel.getMerchantActive()

            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }

            confirmVerified(isLoadingObserver)
            assertNotNull(viewModel.merchant.value)
            assertEquals(viewModel.merchant.value, expectedResult)
        }
    }

    @Test
    fun `updateMerchantInfo should return success message when success`() {

        // Given
        val name = "Merchant Name"
        val address = "Merchant Address"
        val type = "Merchant Type"
        val newPhoto: Uri? = null

        val expectedResponse = "Merchant info updated successfully"

        runTest {

            `when`(updateMerchantInfo.execute(name, address, type, newPhoto)).thenReturn(flow { emit(expectedResponse) })

            // When
            viewModel.updateMerchantInfo(name, address, type, newPhoto)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { messageObserver.onChanged(expectedResponse) }

            confirmVerified(isLoadingObserver, messageObserver)
            assertNotNull(viewModel.message.value)
            assertEquals(viewModel.message.value, expectedResponse)
        }
    }
}
