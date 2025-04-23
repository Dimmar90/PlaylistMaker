package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityAudioPlayerBinding

    private lateinit var playButton: MaterialButton
    private lateinit var playerTime: TextView
    private lateinit var returnButton: ImageButton

    private lateinit var trackCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackDuration: TextView
    private lateinit var collectionName: TextView
    private lateinit var trackReleaseDate: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(this)
        )[PlayerViewModel::class.java]

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playButton = binding.playTrackButton
        playerTime = binding.playerTime
        returnButton = binding.playerReturnButton

        trackCover = binding.trackArtwork
        trackName = binding.songTitle
        artistName = binding.artistName
        trackDuration = binding.trackDuration
        collectionName = binding.trackCollectionName
        trackReleaseDate = binding.trackReleaseDate
        trackGenre = binding.trackPrimaryGenreName
        trackCountry = binding.trackCountry

        viewModel.addTrackCover(this, trackCover)

        viewModel.addTrackTitles(
            trackName,
            artistName,
            trackDuration,
            collectionName,
            trackReleaseDate,
            trackGenre,
            trackCountry
        )

        viewModel.preparePlayer()

        playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        viewModel.observeIsPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                playButton.setIconResource(R.drawable.pause_button)
            } else {
                playButton.setIconResource(R.drawable.play_track_button_icon)
            }
        }

        viewModel.observeCurrentTime.observe(this) { time ->
            playerTime.text = time
        }

        viewModel.observeIsEnded.observe(this) { isTrackEnded ->
            if (isTrackEnded) {
                playButton.setIconResource(R.drawable.play_track_button_icon)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.pausePlayer()
                finish()
            }
        })

        returnButton.setOnClickListener {
            viewModel.pausePlayer()
            finish()
        }
    }
}