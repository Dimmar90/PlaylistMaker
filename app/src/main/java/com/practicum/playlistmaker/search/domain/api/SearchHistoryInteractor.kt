package com.practicum.playlistmaker.search.domain.api

import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.data.dto.TrackDto

interface SearchHistoryInteractor {
    fun getHistory(searchHistoryListRecycler: RecyclerView): MutableList<TrackDto>
    fun saveHistory(historyMap: MutableMap<String, String>)
    fun clearHistory()
}