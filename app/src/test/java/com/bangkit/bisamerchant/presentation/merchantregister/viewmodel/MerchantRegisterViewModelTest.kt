package com.bangkit.bisamerchant.presentation.merchantregister.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.merchantregister.usecase.RegisterMerchant
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
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
class MerchantRegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: MerchantRegisterViewModel

    @Mock
    private lateinit var registerMerchant: RegisterMerchant

    private lateinit var messageObserver: Observer<String>
    private lateinit var isLoadingObserver: Observer<Boolean>
    private lateinit var isRegisterSuccessObserver: Observer<Boolean>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        messageObserver = mockk(relaxed = true)
        isLoadingObserver = mockk(relaxed = true)
        isRegisterSuccessObserver = mockk(relaxed = true)

        viewModel = MerchantRegisterViewModel(registerMerchant)

        viewModel.message.observeForever(messageObserver)
        viewModel.isLoading.observeForever(isLoadingObserver)
        viewModel.isRegisterSuccess.observeForever(isRegisterSuccessObserver)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        viewModel.message.removeObserver(messageObserver)
        viewModel.isLoading.removeObserver(isLoadingObserver)
        viewModel.isRegisterSuccess.removeObserver(isRegisterSuccessObserver)
    }

    @Test
    fun `registerMerchant should update isRegisterSuccess and emit expected response when registration is successful`() {

        // Given
        val name = "Merchant Name"
        val address = "Merchant Address"
        val type = "Merchant Type"
        val photo: Uri = mockk()

        val expectedResponse = "Register merchant successful"

        runTest {
            // When
            `when`(registerMerchant.execute(name, address, type, photo)).thenReturn(flow { emit(expectedResponse) })
            viewModel.registerMerchant(name, address, type, photo)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { isRegisterSuccessObserver.onChanged(true) }
            verify { messageObserver.onChanged(expectedResponse) }

            confirmVerified(
                messageObserver,
                isLoadingObserver,
                isRegisterSuccessObserver
            )

            assertNotNull(viewModel.message.value)
            Assert.assertSame(viewModel.message.value, expectedResponse)
            assertNotNull(viewModel.isRegisterSuccess.value)
            Assert.assertSame(viewModel.isRegisterSuccess.value, true)
        }
    }
}
