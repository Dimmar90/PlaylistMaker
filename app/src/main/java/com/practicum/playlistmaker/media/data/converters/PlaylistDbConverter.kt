package com.practicum.playlistmaker.media.data.converters

import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.models.Playlist
import kotlinx.serialization.json.Json
import org.json.JSONArray

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.coverPath,
            playlist.tracksIds.toString(),
            playlist.tracksAmount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.coverPath,
            toJsonArray(playlist.tracksIds),
            playlist.tracksAmount
        )
    }

    fun toJsonArray(tracksIdsEntity: String): JSONArray {
        val trackIdsArray = JSONArray()

        val tracksIds = Json.decodeFromString<List<String>>(tracksIdsEntity)
        tracksIds.forEach { trackId ->
            trackIdsArray.put(trackId)
        }
        return trackIdsArray
    }
}