package com.practicum.playlistmaker

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

        val track: Track = intent.getParcelableExtra("track")!!
        val playerTime = findViewById<TextView>(R.id.player_time)

        val runnable: Runnable = object : Runnable {
            override fun run() {
                refreshPlayerTime(playerTime)
                handler.postDelayed(this, REFRESH_PLAYER_TIME_DELAY_MILLIS)
            }
        }

        preparePlayer(track.previewUrl)
        returnToMedia(runnable)
        getTrackCover(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
        addTrackTitles(track)
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

    private fun addTrackTitles(track: Track) {
        val songTitle = findViewById<TextView>(R.id.song_title)
        songTitle.text = track.trackName

        val artistName = findViewById<TextView>(R.id.artist_name)
        artistName.text = track.artistName

        val trackDuration = findViewById<TextView>(R.id.track_duration)
        trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        val collectionName = findViewById<TextView>(R.id.track_collectionName)
        collectionName.text = track.collectionName

        val releaseDate = findViewById<TextView>(R.id.track_releaseDate)
        releaseDate.text = track.releaseDate.take(4)

        val primaryGenreName = findViewById<TextView>(R.id.track_primaryGenreName)
        primaryGenreName.text = track.primaryGenreName

        val country = findViewById<TextView>(R.id.track_country)
        country.text = track.country
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
        playerTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
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