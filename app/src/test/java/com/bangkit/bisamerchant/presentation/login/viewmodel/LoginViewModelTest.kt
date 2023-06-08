package com.bangkit.bisamerchant.presentation.login.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.login.usecase.Login
import com.bangkit.bisamerchant.domain.login.usecase.ResetPassword
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
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LoginViewModel

    @Mock
    private lateinit var login: Login

    @Mock
    private lateinit var resetPassword: ResetPassword

    private lateinit var messageObserver: Observer<String?>
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        messageObserver = mockk(relaxed = true)
        isLoadingObserver = mockk(relaxed = true)

        viewModel = LoginViewModel(login, resetPassword)
        viewModel.message.observeForever(messageObserver)
        viewModel.isLoading.observeForever(isLoadingObserver)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        viewModel.message.removeObserver(messageObserver)
        viewModel.isLoading.removeObserver(isLoadingObserver)
    }

    @Test
    fun `login should return success message when user login successfully`() {

        // Given
        val email = "test@example.com"
        val password = "password"
        val expectedMessage = "Login successful"

        runTest {

            `when`(login.execute(email, password)).thenReturn(flow { emit(expectedMessage) })

            // When
            viewModel.login(email, password)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { messageObserver.onChanged(expectedMessage) }

            confirmVerified(isLoadingObserver, messageObserver)

            assertNotNull(viewModel.message.value)
            assertSame(viewModel.message.value, expectedMessage)
        }
    }

    @Test
    fun `resetPassword should return success message when successfully sent reset password`() {

        // Given
        val email = "ega@gmail.com"
        val expectedMessage = "Reset password sent, check your email"

        runTest {

            // When
            `when`(resetPassword.execute(email)).thenReturn(flow { emit(expectedMessage) })
            viewModel.resetPassword(email)

            // Then
            verify { isLoadingObserver.onChanged(true) }
            verify { isLoadingObserver.onChanged(false) }
            verify { messageObserver.onChanged(expectedMessage) }

            confirmVerified(isLoadingObserver, messageObserver)

            assertNotNull(viewModel.message.value)
            assertSame(viewModel.message.value, expectedMessage)
        }
    }
}
