package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import org.json.JSONArray

sealed class PlaylistState {

    data object StateEmpty : PlaylistState()

    data class StateContent(val playlists: List<Playlist>) : PlaylistState()

    data class StatePlaylist(val playlist: Playlist) : PlaylistState()

    data class StateTracksIds(val trackIds: JSONArray) : PlaylistState()

    data class StateTracksList(val tracks: List<Track>) : PlaylistState()

    data class StateIsTrackAdded(val isTrackAdded: Boolean) : PlaylistState()

    data object StateDeletePlaylist: PlaylistState()
}