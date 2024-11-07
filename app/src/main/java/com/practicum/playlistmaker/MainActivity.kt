package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton

const val SETTINGS_REFERENCES = "settings_activity_preferences"

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val sharedPrefs = getSharedPreferences(SETTINGS_REFERENCES, MODE_PRIVATE)
        val app = App()
        val json = sharedPrefs.getString(SETTINGS_KEY, "")

        if (json == "MODE_NIGHT_YES") {
            app.switchTheme(true)
        }

        val searchButton = findViewById<MaterialButton>(R.id.search_button)
        searchButton.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        val mediaButton = findViewById<MaterialButton>(R.id.media_button)
        mediaButton.setOnClickListener {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        val settingsButton = findViewById<MaterialButton>(R.id.settings_button)
        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}