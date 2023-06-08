package com.bangkit.bisamerchant.presentation.register.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.register.usecase.RegisterOwner
import io.mockk.*
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: RegisterViewModel

    @Mock
    private lateinit var registerOwner: RegisterOwner

    private lateinit var messageObserver: Observer<String>
    private lateinit var isLoadingObserver: Observer<Boolean>
    private lateinit var isRegisterSuccessObserver: Observer<Boolean>

    @Before
    fun setup() {
        messageObserver = mockk(relaxed = true)
        isLoadingObserver = mockk(relaxed = true)
        isRegisterSuccessObserver = mockk(relaxed = true)

        viewModel = RegisterViewModel(registerOwner)

        Dispatchers.setMain(testDispatcher)

        viewModel.message.observeForever(messageObserver)
        viewModel.isLoading.observeForever(isLoadingObserver)
        viewModel.isRegisterSuccess.observeForever(isRegisterSuccessObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        viewModel.message.removeObserver(messageObserver)
        viewModel.isLoading.removeObserver(isLoadingObserver)
        viewModel.isRegisterSuccess.removeObserver(isLoadingObserver)
    }

    @Test
    fun `registerOwner should update message LiveData with success message`() {
        // Given
        val name = "John Doe"
        val email = "johndoe@example.com"
        val password = "password"
        val pin = "1234"

        val expectedResponse = "Register successfully"

        runTest {

            // When
            `when`(registerOwner.execute(name, email, password, pin)).thenReturn(flow {
                emit(
                    expectedResponse
                )
            })

            viewModel.registerOwner(name, email, password, pin)

            // Then
            verify { messageObserver.onChanged(expectedResponse) }

            confirmVerified(messageObserver)

            assertNotNull(viewModel.message.value)
            assertSame(viewModel.message.value, expectedResponse)
        }
    }

    @Test
    fun `registerOwner should update message LiveData with error message`() {
        // Given
        val name = "John Doe"
        val email = "johndoe@example.com"
        val password = "password"
        val pin = "1234"

        val expectedResponse = "Registration failed"

        runTest {

            // When
            `when`(registerOwner.execute(name, email, password, pin)).thenReturn(flow {
                emit(
                    expectedResponse
                )
            })

            viewModel.registerOwner(name, email, password, pin)

            // Then
            verify { messageObserver.onChanged(expectedResponse) }

            confirmVerified(messageObserver)

            assertNotNull(viewModel.message.value)
            assertSame(viewModel.message.value, expectedResponse)
        }
    }

    @Test
    fun `registerOwner should update isLoading LiveData with true and false`() {

        // Given
        val name = "John Doe"
        val email = "johndoe@example.com"
        val password = "password"
        val pin = "1234"

        val expectedResponse = "Registration successfully"

        runTest {

            // When
            `when`(registerOwner.execute(name, email, password, pin)).thenReturn(flow {
                emit(
                    expectedResponse
                )
            })

            viewModel.registerOwner(name, email, password, pin)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { messageObserver.onChanged(expectedResponse) }

            confirmVerified(messageObserver, isLoadingObserver)

            assertNotNull(viewModel.message.value)
            assertSame(viewModel.message.value, expectedResponse)
        }
    }

    @Test
    fun `registerOwner should update isRegisterSuccess LiveData with true when registration is successful`() {

        // Given
        val name = "John Doe"
        val email = "johndoe@example.com"
        val password = "password"
        val pin = "1234"

        val expectedResponse = "Register successful"

        runTest {

            // When
            `when`(registerOwner.execute(name, email, password, pin)).thenReturn(flow {
                emit(
                    expectedResponse
                )
            })

            viewModel.registerOwner(name, email, password, pin)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { messageObserver.onChanged(expectedResponse) }
            verify { messageObserver.onChanged(expectedResponse) }

            confirmVerified(
                messageObserver,
                isLoadingObserver,
            )

            assertNotNull(viewModel.isRegisterSuccess.value)
            assertSame(viewModel.isRegisterSuccess.value, true)
        }
    }

    @Test
    fun `registerOwner should update isRegisterSuccess LiveData with false when registration fails`() {

        // Given
        val name = "John Doe"
        val email = "johndoe@example.com"
        val password = "password"
        val pin = "1234"

        val expectedResponse = "Registration failed"

        runTest {

            // When
            `when`(registerOwner.execute(name, email, password, pin)).thenReturn(flow {
                emit(
                    expectedResponse
                )
            })

            viewModel.registerOwner(name, email, password, pin)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { isRegisterSuccessObserver.onChanged(false) }
            verify { messageObserver.onChanged(expectedResponse) }

            confirmVerified(
                messageObserver,
                isLoadingObserver,
                isRegisterSuccessObserver
            )

            assertNotNull(viewModel.isRegisterSuccess.value)
            assertSame(viewModel.isRegisterSuccess.value, false)
        }
    }
}
