package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistRepository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(
        playlistId: Int,
        playlistName: String,
        playlistDescription: String,
        coverPath: String
    ) {
        playlistRepository.updatePlaylist(playlistId, playlistName, playlistDescription, coverPath)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addTrackToMedia(track: Track) {
        playlistRepository.addTrackToMedia(track)
    }

    override suspend fun getMediaTrackById(trackId: String): Flow<Track> {
        return playlistRepository.getMediaTrackById(trackId)
    }

    override suspend fun deleteTrackFromMedia(trackId: String, playlistId: Int) {
        playlistRepository.deleteTrackFromMedia(trackId, playlistId)
    }

    override suspend fun getTracksIds(playlistId: Int): Flow<JSONArray> {
        return playlistRepository.getTracksIds(playlistId)
    }

    override suspend fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun addTracksIds(tracksIds: String, playlistId: Int?) {
        playlistRepository.addTracksIds(tracksIds, playlistId)
    }

    override suspend fun putTracksAmount(tracksAmount: Int, playlistId: Int?) {
        playlistRepository.putTracksAmount(tracksAmount, playlistId)
    }

    override suspend fun getTracksList(playlistId: Int): Flow<MutableList<Track>> {
        return playlistRepository.getTracksList(playlistId)
    }
}