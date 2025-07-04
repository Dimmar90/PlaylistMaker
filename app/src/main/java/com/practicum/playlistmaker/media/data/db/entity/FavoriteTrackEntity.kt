package com.practicum.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks_table")
data class FavoriteTrackEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var trackId: String,
    var trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {
    constructor(
        trackId: String,
        trackName: String,
        artistName: String,
        trackTimeMillis: Int,
        artworkUrl100: String,
        collectionName: String,
        releaseDate: String,
        primaryGenreName: String,
        country: String,
        previewUrl: String
    ) : this(
        null,
        trackId,
        trackName,
        artistName,
        trackTimeMillis,
        artworkUrl100,
        collectionName,
        releaseDate,
        primaryGenreName,
        country,
        previewUrl
    )
}