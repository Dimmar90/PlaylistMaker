package com.practicum.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray

class PlaylistsViewModel(
    private var playlistInteractor: PlaylistInteractor,
    private var searchTracksInteractor: SearchTracksInteractor,
    historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    private var track: Track? = null
    private val tracksList: MutableList<Track> = ArrayList()
    private var searchJob: Job? = null
    private val playlistsState = MutableLiveData<PlaylistState>()
    fun observePlaylistsState(): LiveData<PlaylistState> = playlistsState

    init {
        if (historyInteractor.getHistory().isNotEmpty()) {
            track = historyInteractor.getHistory()[0]
        }
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

    fun getTracksIds(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.getTracksIds(playlistId).collect { tracksIds ->
                playlistsState.postValue(PlaylistState.StateTracksIds(tracksIds))
            }
        }
    }

    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
                playlistsState.postValue(PlaylistState.StatePlaylist(playlist))
            }
        }
    }

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
        }
    }

    fun updatePlaylist(
        playlistId: Int,
        coverPath: String,
        playlistName: String,
        playlistDescription: String
    ) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(
                playlistId,
                playlistName,
                playlistDescription,
                coverPath
            )
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        playlist.tracksIds.put(track?.trackId)
        viewModelScope.launch {
            playlistInteractor.addTracksIds("${playlist.tracksIds}", playlist.id)
            playlistInteractor.putTracksAmount(playlist.tracksIds.length(), playlist.id)
        }
    }

    fun isTrackAdded(playlist: Playlist): Boolean {
        val isTrackAdded = checkIfJsonArrayContainsElement(playlist.tracksIds, track!!.trackId)
        return isTrackAdded
    }

    fun deleteTrack(track: Track, playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->

                if (playlist.tracksIds.length() == 1) {
                    playlist.tracksIds.remove(0)
                }

                for (i in 0 until playlist.tracksIds.length()) {
                    val element = playlist.tracksIds[i]
                    if (element == track.trackId) {
                        playlist.tracksIds.remove(i)
                    }
                }

                playlistInteractor.addTracksIds("${playlist.tracksIds}", playlist.id)
                playlistInteractor.putTracksAmount(playlist.tracksIds.length(), playlist.id)
                playlistsState.postValue(PlaylistState.StateTracksIds(playlist.tracksIds))
            }
        }
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

    fun searchDebounce(trackId: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            getTracksList(trackId)
        }
    }

    private fun getTracksList(trackId: String) {
        tracksList.clear()
        viewModelScope.launch {
            searchTracksInteractor.searchTracksList(trackId).collect { pair ->
                try {
                    tracksList.addAll(pair.first)
                    playlistsState.postValue(PlaylistState.StateTracksList(tracksList))
                } catch (e: NullPointerException) {
                    Log.d("Error", "NullPointerException")
                }
            }
        }
    }

    @SuppressLint("ShowToast", "ResourceAsColor")
    fun showSnackBar(view: View, text: String, context: Context) {
        val mSnackbar: Snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
        val mView = mSnackbar.view
        val textView =
            mView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        if (checkNightModeOn(context)) {
            mSnackbar.setBackgroundTint(R.color.white)
        } else {
            mSnackbar.setBackgroundTint(R.color.button_grey)
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