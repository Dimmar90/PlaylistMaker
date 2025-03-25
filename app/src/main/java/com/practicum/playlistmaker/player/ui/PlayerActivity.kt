package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_PLAYER_TIME_DELAY_MILLIS = 1000L
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    private val handler = Handler(Looper.getMainLooper())

    @Suppress("DEPRECATION")
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val trackName = intent.getStringExtra("trackName").toString()
        val trackPreviewUrl = intent.getStringExtra("previewUrl").toString()
        val trackArtworkUrl = intent.getStringExtra("artworkUrl100")
        val artistName = intent.getStringExtra("artistName").toString()
        val trackDuration = intent.getIntExtra("trackTimeMillis", 0)
        val collectionName = intent.getStringExtra("collectionName").toString()
        val releaseDate = intent.getStringExtra("releaseDate").toString()
        val primaryGenreName = intent.getStringExtra("primaryGenreName").toString()
        val country = intent.getStringExtra("country").toString()
        val playerTime = findViewById<TextView>(R.id.player_time)

        val runnable: Runnable = object : Runnable {
            override fun run() {
                refreshPlayerTime(playerTime)
                handler.postDelayed(this, REFRESH_PLAYER_TIME_DELAY_MILLIS)
            }
        }

        preparePlayer(trackPreviewUrl)
        returnToMedia(runnable)
        getTrackCover(trackArtworkUrl!!.replaceAfterLast('/', "512x512bb.jpg"))
        addTrackTitles(
            trackName,
            artistName,
            trackDuration,
            collectionName,
            releaseDate,
            primaryGenreName,
            country
        )
        playTrack(runnable, playerTime)
        addToMedia()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                pausePlayer(runnable)
                finish()
            }
        })
    }

    private fun returnToMedia(runnable: Runnable) {
        val returnButton = findViewById<ImageButton>(R.id.player_return_button)
        returnButton.setOnClickListener {
            pausePlayer(runnable)
            finish()
        }
    }

    private fun getTrackCover(url: String) {
        val trackCover = findViewById<ImageView>(R.id.track_artwork)
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.placeholder_icon)
            .centerInside()
            .transform(RoundedCorners(8))
            .into(trackCover)
    }

    private fun addTrackTitles(
        trackName: String, artistName: String, trackDuration: Int,
        collectionName: String, releaseDate: String, primaryGenreName: String, country: String
    ) {
        val songTitleView = findViewById<TextView>(R.id.song_title)
        songTitleView.text = trackName

        val artistNameView = findViewById<TextView>(R.id.artist_name)
        artistNameView.text = artistName

        val trackDurationView = findViewById<TextView>(R.id.track_duration)
        trackDurationView.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackDuration)

        val collectionNameView = findViewById<TextView>(R.id.track_collectionName)
        collectionNameView.text = collectionName

        val releaseDateView = findViewById<TextView>(R.id.track_releaseDate)
        releaseDateView.text = releaseDate.take(4)

        val primaryGenreNameView = findViewById<TextView>(R.id.track_primaryGenreName)
        primaryGenreNameView.text = primaryGenreName

        val countryView = findViewById<TextView>(R.id.track_country)
        countryView.text = country
    }

    private fun playTrack(runnable: Runnable, playerTime: TextView) {
        val playTrackButton = findViewById<MaterialButton>(R.id.play_track_button)
        playTrackButton.setOnClickListener {
            playbackControl(runnable)
        }
        mediaPlayer.setOnCompletionListener {
            pausePlayer(runnable)
            playerTime.text = "00:00"
        }
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
        }
    }

    private fun playbackControl(runnable: Runnable) {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer(runnable)
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer(runnable)
            }
        }
    }

    private fun startPlayer(runnable: Runnable) {
        mediaPlayer.start()
        startRunnable(runnable)
        val playTrackButton = findViewById<MaterialButton>(R.id.play_track_button)
        playTrackButton.setIconResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer(runnable: Runnable) {
        mediaPlayer.pause()
        stopRunnable(runnable)
        val playTrackButton = findViewById<MaterialButton>(R.id.play_track_button)
        playTrackButton.setIconResource(R.drawable.play_track_button_icon)
        playerState = STATE_PAUSED
    }

    private fun refreshPlayerTime(playerTime: TextView) {
        playerTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    private fun startRunnable(runnable: Runnable) {
        runnable.run()
    }

    private fun stopRunnable(runnable: Runnable) {
        handler.removeCallbacks(runnable)
    }

    private fun addToMedia() {
        val addToMediaButton = findViewById<MaterialButton>(R.id.add_to_media_button)
        addToMediaButton.setOnClickListener {
            Toast.makeText(this, "Add Track To Media", Toast.LENGTH_SHORT).show()
        }
    }
}