package com.dmitriib.challenge.ui.screens.records

sealed interface RecordsUserAction {
    data class ItemClick(val id: Int) : RecordsUserAction
    data object FabClick : RecordsUserAction
    data class CheckPermissionsResult(val permissions: Map<String, Boolean>) : RecordsUserAction
    data class RequestPermissionsResult(val permissions: Map<String, Boolean>) : RecordsUserAction
    data object NewRecordOpened : RecordsUserAction
}
