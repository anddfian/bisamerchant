package com.bangkit.bisamerchant

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bangkit.bisamerchant.domain.register.usecase.RegisterOwner
import com.bangkit.bisamerchant.presentation.register.viewmodel.RegisterViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
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

class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)
    private val registerSuccessful = "Register successful"

    private lateinit var viewModel: RegisterViewModel
    private lateinit var registerOwner: RegisterOwner



    @Before
    fun setup() {
        registerOwner = mockk(relaxed = true)

        viewModel = RegisterViewModel(registerOwner)

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun `registerOwner should update message LiveData with success message`() = testScope.runBlockingTest {
        // Given
        val name = "John Doe"
        val email = "johndoe@example.com"
        val password = "password"
        val pin = "1234"

        coEvery { registerOwner.execute(name, email, password, pin) } coAnswers { flowOf(
            registerSuccessful
        ) }

        val observer = mockk<Observer<String>>(relaxed = true)
        viewModel.message.observeForever(observer)

        // When
        viewModel.registerOwner(name, email, password, pin)

        // Then
        verify { observer.onChanged(registerSuccessful) }

        coVerify { registerOwner.execute(name, email, password, pin) }

        confirmVerified(observer, registerOwner)
    }

    @Test
    fun `registerOwner should update message LiveData with error message`() = testScope.runBlockingTest {
        // Given
        val name = "John Doe"
        val email = "johndoe@example.com"
        val password = "password"
        val pin = "1234"

        val errorMessage = "Registration failed"
        coEvery { registerOwner.execute(name, email, password, pin) } coAnswers { flowOf(errorMessage) }

        val observer = mockk<Observer<String>>(relaxed = true)
        viewModel.message.observeForever(observer)

        // When
        viewModel.registerOwner(name, email, password, pin)

        // Then
        verify { observer.onChanged(errorMessage) }

        coVerify { registerOwner.execute(name, email, password, pin) }

        confirmVerified(observer, registerOwner)
    }

    @Test
    fun `registerOwner should update isLoading LiveData with true and false`() = testScope.runBlockingTest {
        // Given
        val name = "John Doe"
        val email = "johndoe@example.com"
        val password = "password"
        val pin = "1234"

        coEvery { registerOwner.execute(name, email, password, pin) } coAnswers { flowOf(
            registerSuccessful
        ) }

        val loadingObserver = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isLoading.observeForever(loadingObserver)

        // When
        viewModel.registerOwner(name, email, password, pin)

        // Then
        verifyOrder {
            loadingObserver.onChanged(true)
            loadingObserver.onChanged(false)
        }

        coVerify { registerOwner.execute(name, email, password, pin) }

        confirmVerified(loadingObserver, registerOwner)
    }

    @Test
    fun `registerOwner should update isRegisterSuccess LiveData with true when registration is successful`() = testScope.runBlockingTest {
        // Given
        val name = "John Doe"
        val email = "johndoe@example.com"
        val password = "password"
        val pin = "1234"

        coEvery { registerOwner.execute(name, email, password, pin) } coAnswers { flowOf(
            registerSuccessful
        ) }

        val successObserver = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isRegisterSuccess.observeForever(successObserver)

        // When
        viewModel.registerOwner(name, email, password, pin)

        // Then
        verify { successObserver.onChanged(true) }

        coVerify { registerOwner.execute(name, email, password, pin) }

        confirmVerified(successObserver, registerOwner)
    }

    @Test
    fun `registerOwner should update isRegisterSuccess LiveData with false when registration fails`() = testScope.runBlockingTest {
        // Given
        val name = "John Doe"
        val email = "johndoe@example.com"
        val password = "password"
        val pin = "1234"

        val errorMessage = "Registration failed"
        coEvery { registerOwner.execute(name, email, password, pin) } coAnswers { flowOf(errorMessage) }

        val successObserver = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isRegisterSuccess.observeForever(successObserver)

        // When
        viewModel.registerOwner(name, email, password, pin)

        // Then
        verify { successObserver.onChanged(false) }

        coVerify { registerOwner.execute(name, email, password, pin) }

        confirmVerified(successObserver, registerOwner)
    }
}
