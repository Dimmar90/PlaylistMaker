package com.practicum.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PlaylistsViewModel(private var playlistInteractor: PlaylistInteractor) : ViewModel() {

    init {
        getPlaylists()
    }

    private val playlistsState = MutableLiveData<PlaylistState>()
    fun observePlaylistsState(): LiveData<PlaylistState> = playlistsState

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlist ->
                getPlaylistsState(playlist)
            }
        }
    }

    private fun getPlaylistsState(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            playlistsState.postValue(PlaylistState.StateEmpty)
        } else {
            playlistsState.postValue(PlaylistState.StateContent(playlists))
        }
    }

    @SuppressLint("ShowToast")
    fun showSnackBar(view: View, text: String, context: Context) {
        val mSnackbar: Snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
        val mView = mSnackbar.view
        val textView =
            mView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER)
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL)
        }
        if(checkNightModeOn(context)){
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