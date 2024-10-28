package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val SETTINGS_KEY = "key_for_settings"

class App : Application() {

    private var darkTheme = false

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun putInSharedPreferences() {
        val sharedPrefs: SharedPreferences = getSharedPreferences(SETTINGS_REFERENCES, MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
        if (darkTheme) {
            sharedPrefs.edit()
                .putString(SETTINGS_KEY, "MODE_NIGHT_YES")
                .apply()
        } else {
            sharedPrefs.edit()
                .putString(SETTINGS_KEY, "MODE_NIGHT_NO")
                .apply()
        }
    }
}