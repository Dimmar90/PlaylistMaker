package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table ORDER BY id DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("UPDATE playlists_table SET tracksIds = :tracksIds WHERE id = :playlistId")
    suspend fun addTracksIds(tracksIds: String, playlistId: Long?)

    @Query("UPDATE playlists_table SET tracksAmount = :tracksAmount WHERE id = :playlistId")
    suspend fun putTracksAmount(tracksAmount: Int, playlistId: Long?)
}