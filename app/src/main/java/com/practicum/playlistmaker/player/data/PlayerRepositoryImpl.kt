package com.practicum.playlistmaker.player.data

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.ui.PlayerState
import java.util.Locale

class PlayerRepositoryImpl : PlayerRepository {

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

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
    }

    override fun resetPlayer(runnable: Runnable) {
        mediaPlayer.reset()
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

    override fun refreshPlayerTime(playerTime: TextView) {
        playerTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                .toString()
    }

    @SuppressLint("SetTextI18n")
    override fun setOnCompletionListener(
        playerStateLiveData: MutableLiveData<PlayerState>,
    ) {
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.value = PlayerState.StateTrackEnded
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}