package com.practicum.playlistmaker.player.domain.api

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.player.ui.PlayerState

interface PlayerInteractor {
    fun preparePlayer(url: String)
    fun startPlayer(runnable: Runnable)
    fun pausePlayer(runnable: Runnable)
    fun resetPlayer(runnable: Runnable)
    fun playbackControl(runnable: Runnable): Int
    fun refreshPlayerTime(runnable: Runnable, playerTime: TextView)
    fun setOnCompletionListener(runnable: Runnable, playerTime: TextView, playerStateLiveData: MutableLiveData<PlayerState>)
}