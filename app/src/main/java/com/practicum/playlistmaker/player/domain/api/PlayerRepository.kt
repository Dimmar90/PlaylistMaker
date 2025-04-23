package com.practicum.playlistmaker.player.domain.api

import androidx.lifecycle.MutableLiveData

interface PlayerRepository {
    fun preparePlayer(url: String)
    fun startPlayer(runnable: Runnable)
    fun pausePlayer(runnable: Runnable)
    fun playbackControl(runnable: Runnable): Int
    fun refreshPlayerTime(runnable: Runnable, playerTime: MutableLiveData<String>)
    fun setOnCompletionListener(runnable: Runnable, playerTime: MutableLiveData<String>, isTrackEnded: MutableLiveData<Boolean>)
}