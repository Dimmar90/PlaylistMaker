package com.practicum.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private var favoriteTracksInteractor: FavoriteTracksInteractor,
    private var historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val favoritesState = MutableLiveData<FavoritesState>()
    fun observeFavoritesState(): LiveData<FavoritesState> = favoritesState

    init {
        getFavoriteTracks()
    }

    fun getFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collect { tracks ->
                getFavoritesState(tracks)
            }
        }
    }

    private fun getFavoritesState(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            favoritesState.postValue(FavoritesState.StateEmpty)
        } else {
            favoritesState.postValue(FavoritesState.StateContent(tracks))
        }
    }

    fun addTrackToHistory(track: Track) {
        historyInteractor.addTrackToHistory(track.toTrackDto())
    }
}