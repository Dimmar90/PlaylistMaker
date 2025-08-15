package com.practicum.playlistmaker.media.data.impl

import com.practicum.playlistmaker.media.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.media.data.converters.TrackDbConverter
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONArray

class PlaylistRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistRepository {

    private val playlistDbConverter = PlaylistDbConverter()
    private val trackDbConverter = TrackDbConverter()

    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().addPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        appDatabase.playlistDao().deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(
        playlistId: Int,
        playlistName: String,
        playlistDescription: String,
        coverPath: String
    ) {
        appDatabase.playlistDao()
            .updatePlaylist(playlistId, playlistName, playlistDescription, coverPath)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun addTrackToMedia(track: Track) {
        appDatabase.mediaTracksDao().addTrackToMedia(trackDbConverter.map(track))
    }

    override suspend fun getMediaTrackById(trackId: String): Flow<Track> = flow {
        emit(trackDbConverter.map(appDatabase.mediaTracksDao().getMediaTrackById(trackId)))
    }

    override suspend fun deleteTrackFromMedia(trackId: String) {
        appDatabase.mediaTracksDao().deleteTrackFromMedia(trackId)
    }

    override suspend fun getTracksIds(playlistId: Int): Flow<JSONArray> = flow {
        val tracksIds = appDatabase.playlistDao().getTracksIds(playlistId)
        emit(playlistDbConverter.toJsonArray(tracksIds))
    }

    override suspend fun getPlaylistById(playlistId: Int): Flow<Playlist> = flow {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
        emit(playlistDbConverter.map(playlist))
    }

    override suspend fun addTracksIds(tracksIds: String, playlistId: Int?) {
        appDatabase.playlistDao().addTracksIds(tracksIds, playlistId)
    }

    override suspend fun putTracksAmount(tracksAmount: Int, playlistId: Int?) {
        appDatabase.playlistDao().putTracksAmount(tracksAmount, playlistId)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }
}