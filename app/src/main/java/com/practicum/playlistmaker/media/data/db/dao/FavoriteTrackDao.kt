package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.db.entity.FavoriteTrackEntity

@Dao
interface FavoriteTrackDao {

    @Insert(entity = FavoriteTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteTrack(track: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_tracks_table ORDER BY id DESC")
    suspend fun getFavoriteTracks(): List<FavoriteTrackEntity>

    @Query("SELECT * FROM favorite_tracks_table WHERE trackId = :trackId")
    suspend fun isTrackFavorite(trackId: String): FavoriteTrackEntity

    @Query("DELETE FROM favorite_tracks_table WHERE trackId = :trackId")
    suspend fun deleteFavoriteTrack(trackId: String)
}