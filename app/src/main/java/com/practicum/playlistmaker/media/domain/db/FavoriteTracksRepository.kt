package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addTrackToFavorites(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    fun isTrackFavorite(trackId: String): Flow<Boolean>
    suspend fun deleteTrackFromFavorites(track: Track)
}