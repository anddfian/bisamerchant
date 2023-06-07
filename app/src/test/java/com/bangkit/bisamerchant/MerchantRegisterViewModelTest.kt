package com.bangkit.bisamerchant

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.merchantregister.usecase.RegisterMerchant
import com.bangkit.bisamerchant.presentation.merchantregister.viewmodel.MerchantRegisterViewModel
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
class MerchantRegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var viewModel: MerchantRegisterViewModel
    private lateinit var registerMerchant: RegisterMerchant
    private lateinit var messageObserver: Observer<String>
    private lateinit var isLoadingObserver: Observer<Boolean>
    private lateinit var isRegisterSuccessObserver: Observer<Boolean>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        registerMerchant = mockk(relaxed = true)
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
        testScope.cleanupTestCoroutines()
        viewModel.message.removeObserver(messageObserver)
        viewModel.isLoading.removeObserver(isLoadingObserver)
        viewModel.isRegisterSuccess.removeObserver(isRegisterSuccessObserver)
    }

    @Test
    fun registerMerchant() = testScope.runBlockingTest {
        // Given
        val name = "Merchant Name"
        val address = "Merchant Address"
        val type = "Merchant Type"
        val photo: Uri = mockk()
        val successMessage = "Register merchant successful"

        coEvery { registerMerchant.execute(name, address, type, photo) } returns flowOf(successMessage)

        // When
        viewModel.registerMerchant(name, address, type, photo)

        // Then
        verify { isLoadingObserver.onChanged(true) }
        verify { isLoadingObserver.onChanged(false) }
        verify { messageObserver.onChanged(successMessage) }
        verify { isRegisterSuccessObserver.onChanged(true) }

        confirmVerified(isLoadingObserver, messageObserver, isRegisterSuccessObserver)
    }
}
