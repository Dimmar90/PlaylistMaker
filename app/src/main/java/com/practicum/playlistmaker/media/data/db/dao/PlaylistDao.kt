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

    @Query("DELETE FROM playlists_table WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)

    @Query("UPDATE playlists_table SET playlistName = :playlistName, playlistDescription = :playlistDescription, coverPath = :coverPath WHERE id =:playlistId")
    suspend fun updatePlaylist(playlistId: Int, playlistName: String, playlistDescription: String, coverPath: String)

    @Query("SELECT * FROM playlists_table ORDER BY id DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT tracksIds FROM playlists_table WHERE id = :playlistId")
    suspend fun getTracksIds(playlistId: Int): String

    @Query("SELECT * FROM playlists_table WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity

    @Query("UPDATE playlists_table SET tracksIds = :tracksIds WHERE id = :playlistId")
    suspend fun addTracksIds(tracksIds: String, playlistId: Int?)

    @Query("UPDATE playlists_table SET tracksAmount = :tracksAmount WHERE id = :playlistId")
    suspend fun putTracksAmount(tracksAmount: Int, playlistId: Int?)
}