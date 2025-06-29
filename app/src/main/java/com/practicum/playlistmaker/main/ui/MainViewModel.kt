package com.practicum.playlistmaker.main.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor

class MainViewModel(
    private var settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val switchState = MutableLiveData<Boolean>()

    init {
        switchState.value = getSwitchState()
    }

    private fun getSwitchState(): Boolean {
        return settingsInteractor.loadThemeSwitcher()
    }
}