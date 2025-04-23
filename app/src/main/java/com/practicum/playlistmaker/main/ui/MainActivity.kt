package com.practicum.playlistmaker.main.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var searchButton: MaterialButton
    private lateinit var mediaButton: MaterialButton
    private lateinit var settingsButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(
            this,
            MainViewModel.getViewModelFactory(this)
        )[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchButton = binding.searchButton
        mediaButton = binding.mediaButton
        settingsButton = binding.settingsButton

        viewModel.observeSwitchState().observe(this) {
            viewModel.setTheme()
        }

        searchButton.setOnClickListener {
            viewModel.startSearch(this)
        }

        mediaButton.setOnClickListener {
            viewModel.startMedia(this)
        }

        settingsButton.setOnClickListener {
            viewModel.startSettings(this)
            finish()
        }
    }
}