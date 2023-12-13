package com.dmitriib.challenge.ui.screens

sealed interface RecordUserAction {
    data object Create : RecordUserAction
    data object Start : RecordUserAction
    data object Pause : RecordUserAction
    data object Resume : RecordUserAction
    data object Complete : RecordUserAction
}