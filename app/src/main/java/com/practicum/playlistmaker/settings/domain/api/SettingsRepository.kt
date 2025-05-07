package com.practicum.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun saveThemeSwitcher(isChecked: Boolean)
    fun loadThemeSwitcher(): Boolean
    fun setTheme()
}