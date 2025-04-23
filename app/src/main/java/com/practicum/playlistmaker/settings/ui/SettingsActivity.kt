package com.practicum.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode", "CommitPrefEdits")

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    private lateinit var themeSwitcher: Switch
    private lateinit var sharingButton: TextView
    private lateinit var supportButton: TextView
    private lateinit var termsOfUseButton: TextView
    private lateinit var returnButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(this)
        )[SettingsViewModel::class.java]

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themeSwitcher = binding.themeSwitcher
        sharingButton = binding.sharingButton
        supportButton = binding.supportButton
        termsOfUseButton = binding.termsOfUse
        returnButton = binding.returnButton

        viewModel.observeSwitchState().observe(this) { switcherIsChecked ->
            themeSwitcher.setChecked(switcherIsChecked)
            viewModel.setTheme()
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.putSwitchState(checked)
        }

        sharingButton.setOnClickListener {
            viewModel.sharing(this)
        }

        supportButton.setOnClickListener {
            viewModel.support(this)
        }

        termsOfUseButton.setOnClickListener {
            viewModel.termsOfUse(this)
        }

        returnButton.setOnClickListener {
            viewModel.returnToMain(this)
            finish()
        }
    }
}