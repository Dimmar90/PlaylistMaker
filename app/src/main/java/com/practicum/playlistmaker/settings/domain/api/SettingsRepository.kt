package com.practicum.playlistmaker.settings.domain.api

import android.widget.Switch

interface SettingsRepository {
    fun checkThemeSwitcher(themeSwitcher: Switch)
    fun isThemeSwitcherDarkMode(): Boolean
}