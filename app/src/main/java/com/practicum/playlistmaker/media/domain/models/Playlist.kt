package com.practicum.playlistmaker.media.domain.models

import com.practicum.playlistmaker.search.domain.models.Track

data class Playlist(
    val playlistName: String,
    val playlistDescription: String,
    val coverPath: String,
    val tracks: MutableList<Track>? = mutableListOf(),
    val tracksAmount: Int
)



