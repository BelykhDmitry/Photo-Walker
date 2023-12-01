package com.dmitriib.challenge.ui.screen

import com.dmitriib.challenge.MainDispatcherRule
import com.dmitriib.challenge.domain.ImageInfo
import com.dmitriib.challenge.fake.FakeGetImagesUseCase
import com.dmitriib.challenge.fake.FakeLogger
import com.dmitriib.challenge.fake.FakePermissionManager
import com.dmitriib.challenge.ui.permissions.PermissionManager
import com.dmitriib.challenge.ui.screens.ChallengeMainScreenViewModel
import com.dmitriib.challenge.ui.screens.MainScreenState
import com.dmitriib.challenge.utils.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChallengeMainScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var permissionManager: PermissionManager
    private lateinit var getImagesUseCase: FakeGetImagesUseCase
    private lateinit var logger: Logger
    private lateinit var viewModel: ChallengeMainScreenViewModel

    @Before
    fun setUp() {
        permissionManager = FakePermissionManager()
        getImagesUseCase = FakeGetImagesUseCase()
        logger = FakeLogger()
        viewModel = ChallengeMainScreenViewModel(permissionManager, getImagesUseCase, logger)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun observingImages_whenNewList_thenProduceToState() = runTest {
        val statesList = mutableListOf<MainScreenState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.mainScreenStateFlow.toList(statesList)
        }
        viewModel.onActionButtonClicked()
        viewModel.checkPermissionsResult(emptyMap())
        getImagesUseCase.emit(listOf(testImageInfo))
        advanceUntilIdle()

        assertEquals(4, statesList.size)
        val walkingState = statesList[3]
        assert(walkingState is MainScreenState.WalkInProgress)
        assertEquals(listOf(testImageInfo), (walkingState as MainScreenState.WalkInProgress).images)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun observingImages_whenExceptionThrown_thenNoCrash() = runTest {
        val statesList = mutableListOf<MainScreenState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.mainScreenStateFlow.toList(statesList)
        }
        viewModel.onActionButtonClicked()
        viewModel.checkPermissionsResult(emptyMap())
        getImagesUseCase.emitAll(flow { Exception() })
        advanceUntilIdle()

        assertEquals(3, statesList.size)
    }

    companion object {
        private val testImageInfo = ImageInfo("123")
    }
}