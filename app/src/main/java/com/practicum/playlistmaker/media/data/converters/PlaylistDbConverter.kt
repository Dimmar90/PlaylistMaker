package com.practicum.playlistmaker.media.data.converters

import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.models.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.coverPath,
            null,
            playlist.tracksAmount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.coverPath,
            null,
            playlist.tracksAmount
        )
    }

//    private fun toListId(playlistsTracks: List<Track>): MutableList<String> {
//        return playlistsTracks.map {
//            it.trackId
//        }.toMutableList()
//    }
//
//    private fun getTracksIds(tracks: MutableList<Track>): String? {
//        val tracksIds: String? = null
//        return tracksIds
//    }
}