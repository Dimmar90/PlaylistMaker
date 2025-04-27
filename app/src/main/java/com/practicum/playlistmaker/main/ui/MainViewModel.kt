package com.practicum.playlistmaker.main.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor

class MainViewModel(private var settingsInteractor: SettingsInteractor) :
    ViewModel() {

    private val switchState = MutableLiveData<Boolean>()
    fun observeSwitchState(): LiveData<Boolean> = switchState

    init {
        switchState.value = getSwitchState()
    }

    private fun getSwitchState(): Boolean {
        return settingsInteractor.loadThemeSwitcher()
    }

    fun setTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (switchState.value == true) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        fun getViewModelFactory(
            settingsInteractor: SettingsInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel(settingsInteractor)
            }
        }
    }
}