package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.media.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(private val favoriteTracksRepository: FavoriteTracksRepository) :
    FavoriteTracksInteractor {
    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTracksRepository.addTrackToFavorites(track)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getFavoriteTracks()
    }

    override suspend fun isTrackFavorite(trackId: String): Flow<Boolean> {
        return favoriteTracksRepository.isTrackFavorite(trackId)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        favoriteTracksRepository.deleteTrackFromFavorites(track)
    }
}