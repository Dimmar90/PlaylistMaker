package com.practicum.playlistmaker.player.domain.impl

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import java.util.Locale

class PlayerRepositoryImpl : PlayerRepository {

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

    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            STATE_PREPARED
        }
        playerState = STATE_PREPARED
    }

    override fun startPlayer(runnable: Runnable) {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        runnable.run()
    }

    override fun pausePlayer(runnable: Runnable) {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        handler.removeCallbacks(runnable)
    }

    override fun playbackControl(runnable: Runnable): Int {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer(runnable)
                return STATE_PLAYING
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer(runnable)
                return STATE_PAUSED
            }
        }
        return STATE_PREPARED
    }

    override fun refreshPlayerTime(runnable: Runnable, playerTime: TextView) {
        playerTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        handler.postDelayed(runnable, REFRESH_PLAYER_TIME_DELAY_MILLIS)
    }

    override fun setOnCompletionListener(runnable: Runnable, playerTime: TextView, playTrackButton: MaterialButton) {
        mediaPlayer.setOnCompletionListener {
            pausePlayer(runnable)
            playerTime.text = "00:00"
            playTrackButton.setIconResource(R.drawable.play_track_button_icon)
        }
    }
}