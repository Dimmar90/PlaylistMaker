package com.practicum.playlistmaker.search.data.dto

data class TrackDto(
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
)