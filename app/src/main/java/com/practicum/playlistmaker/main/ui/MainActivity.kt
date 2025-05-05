package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.media.ui.MediaActivity
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchButton = binding.searchButton
        val mediaButton = binding.mediaButton
        val settingsButton = binding.settingsButton

        val searchIntent = Intent(this, SearchActivity::class.java)
        val mediaIntent = Intent(this, MediaActivity::class.java)
        val settingsIntent = Intent(this, SettingsActivity::class.java)

        viewModel.observeSwitchState().observe(this) {
            viewModel.setTheme()
        }

        searchButton.setOnClickListener {
            startActivity(searchIntent)
            finish()
        }

        mediaButton.setOnClickListener {
            startActivity(mediaIntent)
        }

        settingsButton.setOnClickListener {
            startActivity(settingsIntent)
            finish()
        }
    }
}