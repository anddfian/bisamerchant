package com.bangkit.bisamerchant.presentation.splashscreen.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bangkit.bisamerchant.domain.splash.usecase.GetAuthInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SplashViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Mock
    lateinit var getAuthInfo: GetAuthInfo

    private lateinit var splashViewModel: SplashViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        splashViewModel = SplashViewModel(getAuthInfo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAuthInfo should emit Expected response in when user open apps`() {
        val expectedResponse = "Expected response"

        runTest {
            `when`(getAuthInfo.execute()).thenReturn(flow { emit(expectedResponse) })
        }

        splashViewModel.getAuthInfo()
        assert(splashViewModel.message.value == expectedResponse)
    }
}