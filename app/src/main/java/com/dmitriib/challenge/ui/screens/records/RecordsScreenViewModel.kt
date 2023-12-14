package com.dmitriib.challenge.ui.screens.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitriib.challenge.data.RecordsRepository
import com.dmitriib.challenge.ui.permissions.PermissionManager
import com.dmitriib.challenge.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecordsScreenViewModel(
    private val permissionManager: PermissionManager,
    private val logger: Logger,
    private val recordsRepository: RecordsRepository
) : ViewModel() {

    private val _mutableRecordsState = MutableStateFlow<RecordsScreenState>(RecordsScreenState.Initial(emptyList()))
    val screenStateFlow: StateFlow<RecordsScreenState>
        get() = _mutableRecordsState

    init {
        viewModelScope.launch {
            recordsRepository.getRecordsFlow()
                .catch { t ->
                    logger.d("Some Error in records list", t)
                }
                .collect { newItems ->
                    _mutableRecordsState.update {
                        when (it) {
                            is RecordsScreenState.CheckingPermissions -> it.copy(items = newItems)
                            is RecordsScreenState.CreatingNewRecord -> it.copy(items = newItems)
                            is RecordsScreenState.Initial -> it.copy(items = newItems)
                            is RecordsScreenState.RecordCreated ->it.copy(items = newItems)
                            is RecordsScreenState.RequestingPermissions -> it.copy(items = newItems)
                            is RecordsScreenState.OpeningRecord -> it.copy(items = newItems)
                        }
                    }
                }
        }
    }

    fun onUserAction(userAction: RecordsUserAction) {
        logger.d("On User Action: $userAction")
        when (userAction) {
            is RecordsUserAction.CheckPermissionsResult -> checkPermissionsResult(userAction.permissions)
            RecordsUserAction.FabClick -> _mutableRecordsState.update {
                RecordsScreenState.CheckingPermissions(it.items, permissionManager.getRequiredPermissions())
            }
            is RecordsUserAction.ItemClick -> _mutableRecordsState.update {
                RecordsScreenState.OpeningRecord(it.items, userAction.id)
            }
            is RecordsUserAction.RequestPermissionsResult -> requestPermissionsResult(userAction.permissions)
            RecordsUserAction.RecordOpened -> _mutableRecordsState.update { RecordsScreenState.Initial(it.items) }
        }
    }

    private fun checkPermissionsResult(permissions: Map<String, Boolean>) {
        if (permissionManager.onPermissionResult(permissions)) {
            createRecord()
        } else {
            _mutableRecordsState.update {
                RecordsScreenState.RequestingPermissions(it.items, permissionManager.getRequiredPermissions())
            }
        }
    }

    private fun requestPermissionsResult(permissions: Map<String, Boolean>) {
        if (permissionManager.onPermissionResult(permissions)) {
            createRecord()
        } else {
            _mutableRecordsState.update {
                RecordsScreenState.Initial(it.items)
            }
        }
    }

    private fun createRecord() {
        _mutableRecordsState.update {
            RecordsScreenState.CreatingNewRecord(it.items)
        }
        viewModelScope.launch {
            val record = recordsRepository.createRecordAsync()
            _mutableRecordsState.update {
                if (record != null) {
                    RecordsScreenState.RecordCreated(it.items, record.id)
                } else {
                    RecordsScreenState.Initial(it.items)
                }
            }
        }
    }
}
