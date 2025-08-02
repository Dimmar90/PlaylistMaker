package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.media.domain.models.Playlist

sealed class PlaylistState {

    data object StateEmpty : PlaylistState()

    data class StateContent(val playlists: List<Playlist>) : PlaylistState()
}