package com.practicum.playlistmaker

data class Track(
    var trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String
)