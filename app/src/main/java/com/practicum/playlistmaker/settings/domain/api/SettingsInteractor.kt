package com.practicum.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun saveThemeSwitcher(isChecked: Boolean)
    fun loadThemeSwitcher(): Boolean
}