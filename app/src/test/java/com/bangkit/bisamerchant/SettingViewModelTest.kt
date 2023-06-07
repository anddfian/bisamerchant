package com.bangkit.bisamerchant

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bangkit.bisamerchant.domain.setting.usecase.Logout
import com.bangkit.bisamerchant.presentation.setting.viewmodel.SettingViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SettingViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var viewModel: SettingViewModel
    private lateinit var logout: Logout

    @Before
    fun setup() {
        logout = mockk(relaxed = true)

        viewModel = SettingViewModel(logout)

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun `logout should call logout use case`() = testScope.runBlockingTest {
        // When
        viewModel.logout()

        // Then
        coVerify { logout.execute() }
    }
}
