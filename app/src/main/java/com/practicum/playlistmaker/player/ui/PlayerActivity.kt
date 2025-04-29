package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var binding: ActivityAudioPlayerBinding

    private lateinit var playButton: MaterialButton
    private lateinit var playerTime: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playButton = binding.playTrackButton
        playerTime = binding.playerTime
        val returnButton = binding.playerReturnButton

        val trackCover = binding.trackArtwork
        val trackName = binding.songTitle
        val artistName = binding.artistName
        val trackDuration = binding.trackDuration
        val collectionName = binding.trackCollectionName
        val trackReleaseDate = binding.trackReleaseDate
        val trackGenre = binding.trackPrimaryGenreName
        val trackCountry = binding.trackCountry

        viewModel.addTrackCover(trackCover)

        viewModel.addTrackTitles(
            trackName,
            artistName,
            trackDuration,
            collectionName,
            trackReleaseDate,
            trackGenre,
            trackCountry
        )

        viewModel.observePlayerState().observe(this) { playerState ->
            putPlayerState(playerState)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.pausePlayer()
                viewModel.resetPlayer()
                finish()
            }
        })

        returnButton.setOnClickListener {
            viewModel.pausePlayer()
            viewModel.resetPlayer()
            finish()
        }
    }

    private fun putPlayerState(state: PlayerState) {
        when (state) {
            is PlayerState.StateDefault -> defaultState()
            is PlayerState.StatePlaying -> playState(state.playbackTime)
            is PlayerState.StatePaused -> pauseState()
            is PlayerState.StateTrackEnded -> trackIsEndedState()
        }
    }

    private fun defaultState() {
        playButton.setOnClickListener {
            playButton.setIconResource(R.drawable.pause_button)
            viewModel.playbackControl()
        }
    }

    private fun playState(playbackTime: String) {
        playerTime.text = playbackTime
        playButton.setOnClickListener {
            playButton.setIconResource(R.drawable.pause_button)
            viewModel.playbackControl()
        }
    }

    private fun pauseState() {
        playButton.setIconResource(R.drawable.play_track_button_icon)
    }

    @SuppressLint("SetTextI18n")
    private fun trackIsEndedState() {
        playerTime.text = "00:00"
        playButton.setIconResource(R.drawable.play_track_button_icon)
    }
}