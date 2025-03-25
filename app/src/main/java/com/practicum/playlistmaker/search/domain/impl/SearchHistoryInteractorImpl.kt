package com.practicum.playlistmaker.search.domain.impl

import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun getHistory(searchHistoryListRecycler: RecyclerView): MutableList<TrackDto> {
        return repository.getHistory(searchHistoryListRecycler)
    }

    override fun saveHistory(historyMap: MutableMap<String,String>) {
        repository.saveHistory(historyMap)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}