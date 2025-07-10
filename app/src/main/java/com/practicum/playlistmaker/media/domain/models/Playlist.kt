package com.practicum.playlistmaker.media.domain.models

import com.practicum.playlistmaker.search.domain.models.Track
import org.json.JSONArray

data class Playlist(
    val id: Long?,
    val playlistName: String,
    val playlistDescription: String,
    val coverPath: String,
    //val tracksIds: MutableList<String>? = mutableListOf(),
    val tracksIds: JSONArray = JSONArray(),
    var tracksAmount: Int
)



