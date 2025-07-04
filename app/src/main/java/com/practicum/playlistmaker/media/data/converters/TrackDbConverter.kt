package com.practicum.playlistmaker.media.data.converters

import com.practicum.playlistmaker.media.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.search.domain.models.Track

class TrackDbConverter {
    fun map(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    fun map(track: FavoriteTrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}