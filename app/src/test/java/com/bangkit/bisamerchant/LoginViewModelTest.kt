package com.bangkit.bisamerchant

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.login.usecase.Login
import com.bangkit.bisamerchant.domain.login.usecase.ResetPassword
import com.bangkit.bisamerchant.presentation.login.viewmodel.LoginViewModel
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
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var viewModel: LoginViewModel
    private lateinit var login: Login
    private lateinit var resetPassword: ResetPassword
    private lateinit var messageObserver: Observer<String?>
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        login = mockk(relaxed = true)
        resetPassword = mockk(relaxed = true)
        messageObserver = mockk(relaxed = true)
        isLoadingObserver = mockk(relaxed = true)
        viewModel = LoginViewModel(login, resetPassword)
        viewModel.message.observeForever(messageObserver)
        viewModel.isLoading.observeForever(isLoadingObserver)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
        viewModel.message.removeObserver(messageObserver)
        viewModel.isLoading.removeObserver(isLoadingObserver)
    }

    @Test
    fun login() = testScope.runBlockingTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        val successMessage = "Login success"

        coEvery { login.execute(email, password) } returns flowOf(successMessage)

        // When
        viewModel.login(email, password)

        // Then
        verify { isLoadingObserver.onChanged(true) }
        verify { isLoadingObserver.onChanged(false) }
        verify { messageObserver.onChanged(successMessage) }

        confirmVerified(isLoadingObserver, messageObserver)
    }

    @Test
    fun resetPassword() = testScope.runBlockingTest {
        // Given
        val email = "ega@gmail.com"
        val successMessage = "Reset password sent, check your email"

        coEvery { resetPassword.execute(email) } returns flowOf(successMessage)

        // When
        viewModel.resetPassword(email)

        // Then
        verify { isLoadingObserver.onChanged(true) }
        verify { isLoadingObserver.onChanged(false) }
        verify { messageObserver.onChanged(successMessage) }
        verify(exactly = 1) { messageObserver.onChanged(null) }

        confirmVerified(isLoadingObserver, messageObserver)
    }
}
