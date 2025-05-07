package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) :
    ViewModel() {

    private val switchState = MutableLiveData<Boolean>()
    fun observeSwitchState(): LiveData<Boolean> = switchState

    init {
        switchState.value = getSwitchState()
    }

    fun putSwitchState(checked: Boolean) {
        settingsInteractor.saveThemeSwitcher(checked)
        switchState.value = checked
    }

    private fun getSwitchState(): Boolean {
        return settingsInteractor.loadThemeSwitcher()
    }

    fun setTheme() {
        settingsInteractor.setTheme()
    }
}