package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTracksIds(tracksIds: String, playlistId: Long?)
    suspend fun putTracksAmount(tracksAmount: Int, playlistId: Long?)
}