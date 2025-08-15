package com.practicum.playlistmaker.media.domain.models

import org.json.JSONArray

data class Playlist(
    val id: Int?,
    var playlistName: String,
    var playlistDescription: String,
    var coverPath: String,
    val tracksIds: JSONArray = JSONArray(),
    var tracksAmount: Int
)



