package com.practicum.playlistmaker.player.domain.impl

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.ui.PlayerState

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

    override fun resetPlayer(runnable: Runnable) {
        repository.resetPlayer(runnable)
    }

    override fun playbackControl(runnable: Runnable): Int {
        return repository.playbackControl(runnable)
    }

    override fun refreshPlayerTime(playerTime: TextView) {
        repository.refreshPlayerTime(playerTime)
    }

    override fun setOnCompletionListener(
        playerStateLiveData: MutableLiveData<PlayerState>
    ) {
        repository.setOnCompletionListener(playerStateLiveData)
    }
}