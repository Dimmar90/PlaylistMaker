package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.media.data.db.entity.MediaTrackEntity

@Dao
interface MediaTracksDao {

    @Insert(entity = MediaTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToMedia(track: FavoriteTrackEntity)

    @Query("SELECT * FROM media_tracks_table WHERE trackId = :trackId")
    suspend fun getMediaTrackById(trackId: String): FavoriteTrackEntity

    @Query("DELETE FROM media_tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrackFromMedia(trackId: String)
}