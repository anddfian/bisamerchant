package com.bangkit.bisamerchant

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.pin.usecase.ValidateOwnerPin
import com.bangkit.bisamerchant.presentation.pin.viewmodel.PinViewModel
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
class PinViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var viewModel: PinViewModel
    private lateinit var validateOwnerPin: ValidateOwnerPin
    private lateinit var messageObserver: Observer<String>
    private lateinit var isPinValidObserver: Observer<Boolean?>
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        validateOwnerPin = mockk(relaxed = true)
        messageObserver = mockk(relaxed = true)
        isPinValidObserver = mockk(relaxed = true)
        isLoadingObserver = mockk(relaxed = true)
        viewModel = PinViewModel(validateOwnerPin)
        viewModel.message.observeForever(messageObserver)
        viewModel.isPinValid.observeForever(isPinValidObserver)
        viewModel.isLoading.observeForever(isLoadingObserver)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
        viewModel.message.removeObserver(messageObserver)
        viewModel.isPinValid.removeObserver(isPinValidObserver)
        viewModel.isLoading.removeObserver(isLoadingObserver)
    }

    @Test
    fun validateOwnerPinTrue() =
        testScope.runBlockingTest {
            // Given
            val inputPin = 1234
            val pinResult = true

            coEvery { validateOwnerPin.execute(inputPin) } returns flowOf(pinResult)

            // When
            viewModel.validateOwnerPin(inputPin)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { isPinValidObserver.onChanged(pinResult) }

            confirmVerified(isLoadingObserver, isPinValidObserver)
        }

    @Test
    fun validateOwnerPinFalse() =
        testScope.runBlockingTest {
            // Given
            val inputPin = 1234
            val pinResult = false

            coEvery { validateOwnerPin.execute(inputPin) } returns flowOf(pinResult)

            // When
            viewModel.validateOwnerPin(inputPin)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { isPinValidObserver.onChanged(pinResult) }
            verify { isPinValidObserver.onChanged(null) }

            confirmVerified(isLoadingObserver, isPinValidObserver)
        }
}