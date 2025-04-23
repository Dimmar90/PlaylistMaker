package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(context: Context) : ViewModel() {

    private var playerInteractor = Creator.providePlayerInteractor()
    private var historyInteractor = Creator.provideSearchHistoryInteractor(context)

    private val track: Track = historyInteractor.getHistory().first()

    private val isPlaying = MutableLiveData<Boolean>()
    val observeIsPlaying: LiveData<Boolean> = isPlaying
    private val currentTime = MutableLiveData("00:00")
    val observeCurrentTime: LiveData<String> = currentTime
    private val isEnded = MutableLiveData<Boolean>()
    val observeIsEnded: LiveData<Boolean> = isEnded

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            playerInteractor.refreshPlayerTime(this, currentTime)
        }
    }

    fun preparePlayer() {
        playerInteractor.preparePlayer(track.previewUrl)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer(runnable)
        isPlaying.value = true
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer(runnable)
        isPlaying.value = false
    }

    fun playbackControl() {
        if (isPlaying.value == true) {
            pausePlayer()
        } else {
            startPlayer()
        }
        playerInteractor.setOnCompletionListener(runnable, currentTime, isEnded)
    }

    fun addTrackCover(context: Context, trackCover: ImageView) {
        Glide.with(context)
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

    companion object {
        fun getViewModelFactory(
            context: Context
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(context)
            }
        }
    }
}