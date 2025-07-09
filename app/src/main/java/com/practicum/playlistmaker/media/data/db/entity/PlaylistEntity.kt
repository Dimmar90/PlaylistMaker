package com.practicum.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var playlistName: String,
    var playlistDescription: String,
    var coverPath: String,
    var tracksIds: String?,
    var tracksAmount: Int
) {
    constructor(
        playlistName: String,
        playlistDescription: String,
        coverPath: String,
        tracksIds: String?,
        tracksAmount: Int
    ) : this(
        null,
        playlistName,
        playlistDescription,
        coverPath,
        tracksIds,
        tracksAmount
    )
}
