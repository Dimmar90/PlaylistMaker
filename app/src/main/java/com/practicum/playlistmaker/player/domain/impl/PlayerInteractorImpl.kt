package com.practicum.playlistmaker.player.domain.impl

import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

    override fun preparePlayer(url: String) {
        repository.preparePlayer(url)
    }

    override fun startPlayer(runnable: Runnable) {
        repository.startPlayer(runnable)
    }

    override fun pausePlayer(runnable: Runnable) {
        repository.pausePlayer(runnable)
    }

    override fun playbackControl(runnable: Runnable): Int {
        return repository.playbackControl(runnable)
    }

    override fun refreshPlayerTime(runnable: Runnable, playerTime: MutableLiveData<String>) {
        repository.refreshPlayerTime(runnable, playerTime)
    }

    override fun setOnCompletionListener(
        runnable: Runnable,
        playerTime: MutableLiveData<String>,
        isTrackEnded: MutableLiveData<Boolean>
    ) {
        repository.setOnCompletionListener(runnable, playerTime, isTrackEnded)
    }
}