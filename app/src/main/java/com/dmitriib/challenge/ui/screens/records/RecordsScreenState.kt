package com.dmitriib.challenge.ui.screens.records

import com.dmitriib.challenge.data.local.RecordItem

sealed interface RecordsScreenState {
    val items: List<RecordItem>

    data class Initial(override val items: List<RecordItem>) : RecordsScreenState

    data class CheckingPermissions(
        override val items: List<RecordItem>,
        val permissions: List<String>
    ) : RecordsScreenState

    data class RequestingPermissions(
        override val items: List<RecordItem>,
        val permissions: List<String>
    ) : RecordsScreenState

    data class CreatingNewRecord(override val items: List<RecordItem>) : RecordsScreenState

    data class RecordCreated(override val items: List<RecordItem>, val recordId: Int) : RecordsScreenState
}
