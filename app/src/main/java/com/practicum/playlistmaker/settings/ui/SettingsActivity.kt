package com.practicum.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.main.ui.MainActivity

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode", "CommitPrefEdits")

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(
                Creator.provideSettingsInteractor(this)
            )
        )[SettingsViewModel::class.java]

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val themeSwitcher = binding.themeSwitcher
        val sharingButton = binding.sharingButton
        val supportButton = binding.supportButton
        val termsOfUseButton = binding.termsOfUse
        val returnButton = binding.returnButton

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)

        val offerIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.offer)))

        val mainIntent = Intent(this, MainActivity::class.java)

        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(getString(R.string.support_mail))
        )
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
        supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))

        viewModel.observeSwitchState().observe(this) { switcherIsChecked ->
            themeSwitcher.setChecked(switcherIsChecked)
            viewModel.setTheme()
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.putSwitchState(checked)
        }

        sharingButton.setOnClickListener {
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            startActivity(supportIntent)
        }

        termsOfUseButton.setOnClickListener {
            startActivity(offerIntent)
        }

        returnButton.setOnClickListener {
            startActivity(mainIntent)
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(mainIntent)
                finish()
            }
        })
    }
}