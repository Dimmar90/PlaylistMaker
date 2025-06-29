package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun isTrackFavorite(trackId: String): Flow<Boolean>
    suspend fun deleteTrackFromFavorites(track: Track)
}