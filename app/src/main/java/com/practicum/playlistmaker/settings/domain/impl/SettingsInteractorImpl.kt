package com.practicum.playlistmaker.settings.domain.impl

import android.widget.Switch
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun checkThemeSwitcher(themeSwitcher: Switch) {
        repository.checkThemeSwitcher(themeSwitcher)
    }

    override fun isThemeSwitcherDarkMode(): Boolean {
        return repository.isThemeSwitcherDarkMode()
    }
}