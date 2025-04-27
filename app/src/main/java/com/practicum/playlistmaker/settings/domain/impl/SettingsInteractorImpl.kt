package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun saveThemeSwitcher(isChecked: Boolean) {
        repository.saveThemeSwitcher(isChecked)
    }

    override fun loadThemeSwitcher(): Boolean {
        return repository.loadThemeSwitcher()
    }
}