package com.practicum.playlistmaker.settings.domain.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {
    override fun saveThemeSwitcher(isChecked: Boolean) {
        sharedPrefs.edit().putBoolean("switchState", isChecked).apply()
    }

    override fun loadThemeSwitcher(): Boolean {
        val isThemeSwitcherDayMode = sharedPrefs.getBoolean("switchState", true)
        return isThemeSwitcherDayMode
    }
}