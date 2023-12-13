package com.dmitriib.challenge.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitriib.challenge.domain.RecordManager
import com.dmitriib.challenge.domain.RecordState
import com.dmitriib.challenge.ui.permissions.PermissionManager
import com.dmitriib.challenge.utils.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChallengeMainScreenViewModel(
    private val permissionManager: PermissionManager,
    private val logger: Logger,
    private val recordManager: RecordManager
) : ViewModel() {

    private var job: Job? = null

    init {
        job = observeState()
    }

    private val _mainScreenStateFlow: MutableStateFlow<MainScreenState> = MutableStateFlow(
        MainScreenState.Initial
    )
    val mainScreenStateFlow: StateFlow<MainScreenState> = _mainScreenStateFlow

    fun checkPermissionsResult(permissions: Map<String, Boolean>) {
        if (permissionManager.onPermissionResult(permissions)) {
            createRecord()
        } else {
            _mainScreenStateFlow.update {
                MainScreenState.RequestingPermissions(
                    permissionManager.getRequiredPermissions()
                )
            }
        }
    }

    fun requestPermissionsResult(permissions: Map<String, Boolean>) {
        if (permissionManager.onPermissionResult(permissions)) {
            createRecord()
        } else {
            _mainScreenStateFlow.update {
                MainScreenState.Initial
            }
        }
    }

    fun onActionButtonClicked(userAction: RecordUserAction) {
        reduceUserAction(userAction)
    }

    private fun reduceUserAction(userAction: RecordUserAction) {
        val currentState = _mainScreenStateFlow.value
        when (userAction) {
            RecordUserAction.Create -> when (currentState) {
                MainScreenState.Initial,
                is MainScreenState.Stopped -> checkPermissionsState()
                else -> Unit
            }
            RecordUserAction.Pause -> recordManager.pauseRecord()
            RecordUserAction.Resume -> recordManager.resumeRecord()
            RecordUserAction.Start -> recordManager.startRecord()
            RecordUserAction.Complete -> recordManager.completeRecord()
        }
    }

    private fun checkPermissionsState() {
        _mainScreenStateFlow.update {
            MainScreenState.CheckingPermissions(
                permissionManager.getRequiredPermissions()
            )
        }
    }

    private fun createRecord() {
        recordManager.createRecord()
    }

    private fun observeState(): Job {
        return viewModelScope.launch {
            recordManager.getRecordStatusFlow()
                .onStart {
                    logger.d("VM: on start")
                }
                .onCompletion {
                    logger.d("VM: on complete")
                }
                .onEach {
                    logger.d("new state before all $it")
                }
                .catch { t ->
                    logger.d("Error occurred while observing record status", t)
                }.collect { state ->
                    logger.d("On new state: $state")
                    val newState = when (state) {
                        is RecordState.Completed -> MainScreenState.Stopped(state.images)
                        is RecordState.Created -> MainScreenState.Created
                        is RecordState.NoCurrent -> MainScreenState.Initial
                        is RecordState.Paused -> MainScreenState.WalkPaused(state.images)
                        is RecordState.Started -> MainScreenState.WalkInProgress(state.images)
                    }
                    _mainScreenStateFlow.update {
                        newState
                    }
                }
        }
    }
}
