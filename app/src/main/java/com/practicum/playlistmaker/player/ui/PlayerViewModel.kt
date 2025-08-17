package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.PlayerState.StatePlaying
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private var application: Application,
    private var playerInteractor: PlayerInteractor,
    private var favoriteTracksInteractor: FavoriteTracksInteractor,
    historyInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private val track: Track = historyInteractor.getHistory()[0]

    private val playerState = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private var progressJob: Job? = null

    @SuppressLint("StaticFieldLeak")
    private val currentTime = TextView(application)

    private val runnable: Runnable = Runnable { refreshPlayerTime() }

    init {
        isTrackFavorite()
    }

    private fun preparePlayer(isTrackFavorite: Boolean) {
        progressJob?.cancel()
        playerInteractor.preparePlayer(track.previewUrl)
        playerState.postValue(PlayerState.StateDefault(isTrackFavorite))
    }

    private fun startPlayer() {
        playerInteractor.startPlayer(runnable)
        playerState.postValue(StatePlaying(currentTime.text.toString()))
    }

    fun pausePlayer() {
        progressJob?.cancel()
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

            is StatePlaying -> {
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
        playerInteractor.setOnCompletionListener(playerState)
    }

    private fun refreshPlayerTime() {
        progressJob = viewModelScope.launch {
            while (isActive) {
                playerInteractor.refreshPlayerTime(currentTime)
                playerState.postValue(StatePlaying(currentTime.text.toString()))
                delay(REFRESH_PLAYER_TIME_DELAY_MILLIS)
                if (currentTime.text.toString() == "00:29") {
                    progressJob?.cancel()
                }
            }
        }
    }

    fun addTrackCover(trackCover: ImageView, drawable: Drawable?) {

        Glide.with(application.applicationContext)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(drawable)
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

    fun addTrackToFavorites() {
        viewModelScope.launch {
            favoriteTracksInteractor.addTrackToFavorites(track)
        }
        playerState.postValue(PlayerState.StateDefault(true))
    }

    fun deleteTrackFromFavorites() {
        viewModelScope.launch {
            favoriteTracksInteractor.deleteTrackFromFavorites(track)
        }
        playerState.postValue(PlayerState.StateDefault(false))
    }

    private fun isTrackFavorite() {
        viewModelScope.launch {
            favoriteTracksInteractor.isTrackFavorite(track.trackId).collect { isFavorite ->
                preparePlayer(isFavorite)
            }
        }
    }

    companion object {
        private const val REFRESH_PLAYER_TIME_DELAY_MILLIS = 300L
    }
}