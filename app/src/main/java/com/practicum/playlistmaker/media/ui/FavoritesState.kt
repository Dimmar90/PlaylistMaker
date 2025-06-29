package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed class FavoritesState {
    data object StateEmpty : FavoritesState()
    data class StateContent(val tracks: List<Track>) : FavoritesState()
}