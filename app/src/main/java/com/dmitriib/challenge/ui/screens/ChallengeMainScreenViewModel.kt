package com.dmitriib.challenge.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitriib.challenge.domain.GetImagesUseCase
import com.dmitriib.challenge.domain.ImageInfo
import com.dmitriib.challenge.domain.RecordManager
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
    private val logger: Logger,
    private val recordManager: RecordManager
) : ViewModel() {

    init {
        recordManager.createRecord()
    }

    private var currentJob: Job? = null

    private val _mainScreenStateFlow: MutableStateFlow<MainScreenState> = MutableStateFlow(
        MainScreenState.Initial
    )
    val mainScreenStateFlow: StateFlow<MainScreenState> = _mainScreenStateFlow

    fun checkPermissionsResult(permissions: Map<String, Boolean>) {
        _mainScreenStateFlow.update {
            if (permissionManager.onPermissionResult(permissions)) {
                create()
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
                create()
            } else {
                MainScreenState.Initial
            }
        }
    }

    fun onActionButtonClicked(userAction: RecordUserAction) {
        reduceUserAction(userAction)
    }

    private fun reduceUserAction(userAction: RecordUserAction) {
        _mainScreenStateFlow.update { currentState ->
            when (userAction) {
                RecordUserAction.Create -> when (currentState) {
                    MainScreenState.Initial,
                    is MainScreenState.Stopped -> checkPermissionsState()
                    else -> null
                }
                RecordUserAction.Pause -> if (currentState is MainScreenState.WalkInProgress) pauseWalkState(currentState.images) else null
                RecordUserAction.Resume -> if (currentState is MainScreenState.WalkPaused) resumeWalkState(currentState.images) else null
                RecordUserAction.Start -> if (currentState is MainScreenState.Created) {
                    recordManager.startRecord()
                    resumeWalkState(emptyList())
                } else null
                RecordUserAction.Complete -> when (currentState) {
                    is MainScreenState.WalkInProgress -> stopWalkingState(currentState.images)
                    is MainScreenState.WalkPaused -> stopWalkingState(currentState.images)
                    else -> null
                }
            } ?: currentState
        }
    }

    private fun checkPermissionsState(): MainScreenState {
        return MainScreenState.CheckingPermissions(
            permissionManager.getRequiredPermissions()
        )
    }

    private fun create(): MainScreenState {
        recordManager.createRecord()
        return MainScreenState.Created
    }

    private fun pauseWalkState(images: List<ImageInfo>): MainScreenState {
        currentJob?.cancel()
        recordManager.pauseRecord()
        return MainScreenState.WalkPaused(images)
    }

    private fun resumeWalkState(images: List<ImageInfo>): MainScreenState {
        startObserving()
        recordManager.resumeRecord()
        return MainScreenState.WalkInProgress(images)
    }

    private fun stopWalkingState(images: List<ImageInfo>): MainScreenState {
        currentJob?.cancel()
        recordManager.completeRecord()
        return MainScreenState.Stopped(images)
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
