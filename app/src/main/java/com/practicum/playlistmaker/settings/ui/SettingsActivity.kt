package com.practicum.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.application.App
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsInteractor = Creator.provideSettingsInteractor(this)

        val switchState = settingsInteractor.isThemeSwitcherDarkMode()

        val themeSwitcher = findViewById<Switch>(R.id.themeSwitcher)

        themeSwitcher.setChecked(switchState)

        val returnButton = findViewById<MaterialButton>(R.id.return_button)
        returnButton.setOnClickListener {
            finish()
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            (applicationContext as App).putInSharedPreferences()
            settingsInteractor.checkThemeSwitcher(themeSwitcher)
        }

        val sharingButton = findViewById<TextView>(R.id.sharing_button)
        sharingButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        val supportButton = findViewById<TextView>(R.id.support_button)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_mail)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            startActivity(supportIntent)
        }

        val termsOfUseButton = findViewById<TextView>(R.id.terms_of_use)
        termsOfUseButton.setOnClickListener {
            val offerIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.offer)))
            startActivity(offerIntent)
        }
    }
}