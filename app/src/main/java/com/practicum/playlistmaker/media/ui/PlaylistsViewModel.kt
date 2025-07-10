package com.practicum.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch
import org.json.JSONArray

class PlaylistsViewModel(
    private var playlistInteractor: PlaylistInteractor,
    historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val track: Track = historyInteractor.getHistory()[0]
    private val playlistsState = MutableLiveData<PlaylistState>()
    fun observePlaylistsState(): LiveData<PlaylistState> = playlistsState

    init {
        getPlaylists()
    }

    private fun getPlaylistsState(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            playlistsState.postValue(PlaylistState.StateEmpty)
        } else {
            playlistsState.postValue(PlaylistState.StateContent(playlists))
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlist ->
                getPlaylistsState(playlist)
            }
        }
    }

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        playlist.tracksIds.put(track.trackId)
        viewModelScope.launch {
            playlistInteractor.addTracksIds("${playlist.tracksIds}", playlist.id)
            playlistInteractor.putTracksAmount(playlist.tracksAmount + 1, playlist.id)
        }
    }

    fun isTrackAdded(playlist: Playlist): Boolean {
        val isTrackAdded = checkIfJsonArrayContainsElement(playlist.tracksIds, track.trackId)
        return isTrackAdded
    }

    private fun checkIfJsonArrayContainsElement(jsonArray: JSONArray, targetValue: Any): Boolean {
        for (i in 0 until jsonArray.length()) {
            val element = jsonArray.get(i)
            if (element == targetValue) {
                return true
            }
        }
        return false
    }

    @SuppressLint("ShowToast")
    fun showSnackBar(view: View, text: String, context: Context) {
        val mSnackbar: Snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
        val mView = mSnackbar.view
        val textView =
            mView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        if (checkNightModeOn(context)) {
            mSnackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
        } else {
            mSnackbar.setBackgroundTint(Color.parseColor("#1A1B22"))
        }
        mSnackbar.show()
    }

    private fun checkNightModeOn(context: Context): Boolean {
        var isNightModeOn = false
        when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> isNightModeOn = true
            Configuration.UI_MODE_NIGHT_NO -> isNightModeOn = false
        }
        return isNightModeOn
    }
}