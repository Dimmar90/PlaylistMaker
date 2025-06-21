package com.practicum.playlistmaker.player.domain.api

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.player.ui.PlayerState

interface PlayerRepository {
    fun preparePlayer(url: String)
    fun startPlayer(runnable: Runnable)
    fun pausePlayer(runnable: Runnable)
    fun resetPlayer(runnable: Runnable)
    fun playbackControl(runnable: Runnable): Int
    fun refreshPlayerTime(playerTime: TextView)
    fun setOnCompletionListener(playerStateLiveData: MutableLiveData<PlayerState>)
}