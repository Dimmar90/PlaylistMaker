package com.practicum.playlistmaker.settings.domain.impl

import android.content.SharedPreferences
import android.widget.Switch
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {
    override fun checkThemeSwitcher(themeSwitcher: Switch) {
        sharedPrefs.edit().putBoolean("switchState", themeSwitcher.isChecked).apply()
    }

    override fun isThemeSwitcherDarkMode(): Boolean {
        val isThemeSwitcherDayMode = sharedPrefs.getBoolean("switchState", true)
        return isThemeSwitcherDayMode
    }
}