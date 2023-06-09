package com.bangkit.bisamerchant.presentation.pin.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.pin.usecase.ValidateOwnerPin
import io.mockk.*
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PinViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: PinViewModel

    @Mock
    private lateinit var validateOwnerPin: ValidateOwnerPin

    private lateinit var messageObserver: Observer<String>
    private lateinit var isPinValidObserver: Observer<Boolean?>
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = PinViewModel(validateOwnerPin)

        messageObserver = mockk(relaxed = true)
        isPinValidObserver = mockk(relaxed = true)
        isLoadingObserver = mockk(relaxed = true)

        viewModel.message.observeForever(messageObserver)
        viewModel.isPinValid.observeForever(isPinValidObserver)
        viewModel.isLoading.observeForever(isLoadingObserver)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()

        viewModel.message.removeObserver(messageObserver)
        viewModel.isPinValid.removeObserver(isPinValidObserver)
        viewModel.isLoading.removeObserver(isLoadingObserver)
    }

    @Test
    fun `validateOwnerPin should return true when owner pin is valid`() {
        // Given
        val inputPin = 123456
        val pinResult = true

        runTest {
            `when`(validateOwnerPin.execute(inputPin)).thenReturn(flow { emit(pinResult) })

            // When
            viewModel.validateOwnerPin(inputPin)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { isPinValidObserver.onChanged(pinResult) }

            confirmVerified(isLoadingObserver, isPinValidObserver)
            assertNotNull(viewModel.isPinValid.value)
            assertEquals(viewModel.isPinValid.value, pinResult)
        }
    }

    @Test
    fun `validateOwnerPin should return true when owner pin is invalid`() {
        // Given
        val inputPin = 123457
        val pinResult = false

        runTest {
            `when`(validateOwnerPin.execute(inputPin)).thenReturn(flow { emit(pinResult) })

            // When
            viewModel.validateOwnerPin(inputPin)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { isPinValidObserver.onChanged(pinResult) }
            verify { isPinValidObserver.onChanged(null) }

            confirmVerified(isLoadingObserver, isPinValidObserver)
            assertNull(viewModel.isPinValid.value)
            assertEquals(viewModel.isPinValid.value, null)
        }
    }
}