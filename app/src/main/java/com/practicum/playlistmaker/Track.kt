package com.practicum.playlistmaker

data class Track(
    var trackId: String,
    var trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String
)