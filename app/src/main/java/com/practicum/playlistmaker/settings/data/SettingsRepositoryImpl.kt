package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {
    override fun saveThemeSwitcher(isChecked: Boolean) {
        sharedPrefs.edit().putBoolean("switchState", isChecked).apply()
    }

    override fun loadThemeSwitcher(): Boolean {
        val isThemeSwitcherNightMode = sharedPrefs.getBoolean("switchState", true)
        return isThemeSwitcherNightMode
    }

    override fun setTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (loadThemeSwitcher()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}