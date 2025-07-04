package com.practicum.playlistmaker.media.data.impl

import com.practicum.playlistmaker.media.data.converters.TrackDbConverter
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.media.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.media.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(private val appDatabase: AppDatabase) :
    FavoriteTracksRepository {

    private val trackDbConverter = TrackDbConverter()

    override suspend fun addTrackToFavorites(track: Track) {
        try {
            appDatabase.favoriteTrackDao().isTrackFavorite(track.trackId)
        } catch (e: IllegalStateException) {
            appDatabase.favoriteTrackDao().addFavoriteTrack(trackDbConverter.map(track))
        }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val favoriteTracks = appDatabase.favoriteTrackDao().getFavoriteTracks()
        emit(convertFromTracksEntity(favoriteTracks))
    }

    private fun convertFromTracksEntity(favoriteTracks: List<FavoriteTrackEntity>): List<Track> {
        return favoriteTracks.map { favoriteTrack -> trackDbConverter.map(favoriteTrack) }
    }

    override fun isTrackFavorite(trackId: String): Flow<Boolean> = flow {
        var isTrackFavorite: Boolean
        try {
            appDatabase.favoriteTrackDao().isTrackFavorite(trackId)
            isTrackFavorite = true
        } catch (e: IllegalStateException) {
            isTrackFavorite = false
        }
        emit(isTrackFavorite)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        appDatabase.favoriteTrackDao().deleteFavoriteTrack(track.trackId)
    }
}