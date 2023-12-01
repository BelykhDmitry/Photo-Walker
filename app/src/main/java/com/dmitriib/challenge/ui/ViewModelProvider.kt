package com.dmitriib.challenge.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dmitriib.challenge.ChallengeApplication
import com.dmitriib.challenge.ui.screens.ChallengeMainScreenViewModel

object ViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            with(application().appContainer) {
                ChallengeMainScreenViewModel(
                    permissionManager,
                    getImagesUseCase,
                    logger
                )
            }
        }
    }
}

private fun CreationExtras.application(): ChallengeApplication {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ChallengeApplication
}
