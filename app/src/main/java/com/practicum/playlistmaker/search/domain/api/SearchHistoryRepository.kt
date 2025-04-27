package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrackToHistory(track: TrackDto)
    fun clearHistory()
}