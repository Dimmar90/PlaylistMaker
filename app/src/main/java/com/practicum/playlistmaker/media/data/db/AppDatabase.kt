package com.practicum.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.media.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.media.data.db.dao.MediaTracksDao
import com.practicum.playlistmaker.media.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.media.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.media.data.db.entity.MediaTrackEntity
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity

@Database(
    version = 1,
    entities = [FavoriteTrackEntity::class, PlaylistEntity::class, MediaTrackEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun mediaTracksDao(): MediaTracksDao
}
