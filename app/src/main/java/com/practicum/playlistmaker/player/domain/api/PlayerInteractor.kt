package com.practicum.playlistmaker.player.domain.api

import android.widget.TextView
import com.google.android.material.button.MaterialButton

interface PlayerInteractor {
    fun preparePlayer(url: String)
    fun startPlayer(runnable: Runnable)
    fun pausePlayer(runnable: Runnable)
    fun playbackControl(runnable: Runnable): Int
    fun refreshPlayerTime(runnable: Runnable, playerTime: TextView)
    fun setOnCompletionListener(runnable: Runnable, playerTime: TextView, playTrackButton: MaterialButton)
}