package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addTracksIds(tracksIds: String, playlistId: Long?) {
        playlistRepository.addTracksIds(tracksIds, playlistId)
    }

    override suspend fun putTracksAmount(tracksAmount: Int, playlistId: Long?) {
        playlistRepository.putTracksAmount(tracksAmount, playlistId)
    }
}