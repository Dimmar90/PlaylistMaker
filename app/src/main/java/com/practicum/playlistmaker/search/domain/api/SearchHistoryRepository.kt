package com.practicum.playlistmaker.search.domain.api

import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.data.dto.TrackDto

interface SearchHistoryRepository {
    fun getHistory(searchHistoryListRecycler: RecyclerView): MutableList<TrackDto>
    fun saveHistory(historyMap: MutableMap<String, String>)
    fun clearHistory()
}