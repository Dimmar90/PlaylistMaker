package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun updatePlaylist(playlistId: Int, playlistName: String, playlistDescription: String, coverPath: String)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getTracksIds(playlistId: Int): Flow<JSONArray>
    suspend fun getPlaylistById(playlistId: Int): Flow<Playlist>
    suspend fun addTracksIds(tracksIds: String, playlistId: Int?)
    suspend fun putTracksAmount(tracksAmount: Int, playlistId: Int?)
}