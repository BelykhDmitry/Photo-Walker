package com.dmitriib.challenge.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitriib.challenge.domain.GetImagesUseCase
import com.dmitriib.challenge.domain.ImageInfo
import com.dmitriib.challenge.ui.permissions.PermissionManager
import com.dmitriib.challenge.utils.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ChallengeMainScreenViewModel(
    private val permissionManager: PermissionManager,
    private val getImagesUseCase: GetImagesUseCase,
    private val logger: Logger
) : ViewModel() {

    private var currentJob: Job? = null

    private val _mainScreenStateFlow: MutableStateFlow<MainScreenState> = MutableStateFlow(
        MainScreenState.Initial
    )
    val mainScreenStateFlow: StateFlow<MainScreenState> = _mainScreenStateFlow

    fun checkPermissionsResult(permissions: Map<String, Boolean>) {
        _mainScreenStateFlow.update {
            if (permissionManager.onPermissionResult(permissions)) {
                resumeWalkState(emptyList())
            } else {
                MainScreenState.RequestingPermissions(
                    permissionManager.getRequiredPermissions()
                )
            }
        }
    }

    fun requestPermissionsResult(permissions: Map<String, Boolean>) {
        _mainScreenStateFlow.update {
            if (permissionManager.onPermissionResult(permissions)) {
                resumeWalkState(emptyList())
            } else {
                MainScreenState.Initial
            }
        }
    }

    fun onActionButtonClicked() {
        _mainScreenStateFlow.update { currentState ->
            when (currentState) {
                MainScreenState.Initial -> checkPermissionsState()
                is MainScreenState.WalkInProgress -> pauseWalkState(currentState.images)
                is MainScreenState.WalkPaused -> resumeWalkState(currentState.images)
                is MainScreenState.CheckingPermissions,
                is MainScreenState.RequestingPermissions -> currentState
            }
        }
    }

    private fun checkPermissionsState(): MainScreenState {
        return MainScreenState.CheckingPermissions(
            permissionManager.getRequiredPermissions()
        )
    }

    private fun pauseWalkState(images: List<ImageInfo>): MainScreenState {
        currentJob?.cancel()
        return MainScreenState.WalkPaused(images)
    }

    private fun resumeWalkState(images: List<ImageInfo>): MainScreenState {
        startObserving()
        return MainScreenState.WalkInProgress(images)
    }

    private fun startObserving() {
        currentJob = viewModelScope.launch {
            getImagesUseCase()
                .catch { t ->
                    logger.d("Error occurred while observing Images", t)
                }
                .collect { images ->
                _mainScreenStateFlow.update { state ->
                    (state as? MainScreenState.WalkInProgress)?.copy(images = images)
                        ?: state
                }
            }
        }
    }
}
