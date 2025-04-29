package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.app.Application
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private var application: Application,
    private var playerInteractor: PlayerInteractor,
    historyInteractor: SearchHistoryInteractor,
    ) : ViewModel() {

    private val track: Track = historyInteractor.getHistory().first()

    private val playerState = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerState

    @SuppressLint("StaticFieldLeak")
    private val currentTime = TextView(application)

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            playerInteractor.refreshPlayerTime(this, currentTime)
            playerState.postValue(PlayerState.StatePlaying(currentTime.text.toString()))
        }
    }

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer(track.previewUrl)
        playerState.postValue(PlayerState.StateDefault)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer(runnable)
        playerState.postValue(PlayerState.StatePlaying(currentTime.text.toString()))
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer(runnable)
        playerState.postValue(PlayerState.StatePaused)
    }

    fun resetPlayer() {
        playerInteractor.resetPlayer(runnable)
    }

    fun playbackControl() {
        when (playerState.value) {
            is PlayerState.StateDefault -> {
                startPlayer()
            }

            is PlayerState.StatePlaying -> {
                pausePlayer()
            }

            is PlayerState.StatePaused -> {
                startPlayer()
            }

            is PlayerState.StateTrackEnded -> {
                startPlayer()
            }

            null -> {}
        }
        playerInteractor.setOnCompletionListener(runnable, currentTime, playerState)
    }

    fun addTrackCover(trackCover: ImageView) {
        Glide.with(application.applicationContext)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder_icon)
            .centerInside()
            .transform(RoundedCorners(8))
            .into(trackCover)
    }

    fun addTrackTitles(
        trackName: TextView,
        artistName: TextView,
        trackDuration: TextView,
        collectionName: TextView,
        trackReleaseDate: TextView,
        trackGenre: TextView,
        trackCountry: TextView
    ) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        collectionName.text = track.collectionName
        trackReleaseDate.text = track.releaseDate.take(4)
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country
    }
}