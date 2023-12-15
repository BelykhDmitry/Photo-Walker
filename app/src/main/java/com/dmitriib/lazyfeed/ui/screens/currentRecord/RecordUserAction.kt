package com.dmitriib.lazyfeed.ui.screens.currentRecord

sealed interface RecordUserAction {
    data object Start : RecordUserAction
    data object Pause : RecordUserAction
    data object Resume : RecordUserAction
    data object Complete : RecordUserAction
}