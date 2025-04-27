package com.practicum.playlistmaker.search.data.impl

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : SearchHistoryRepository {

    private val builder = GsonBuilder()
    private val gson = builder.create()
    private val searchHistoryList = getHistory().toMutableList()

    override fun getHistory(): List<Track> {
        var historyList = listOf<Track>()
        val json = sharedPrefs.getString(SEARCH_KEY, "")
        if (json?.isNotEmpty()!!) {
            historyList =
                gson.fromJson(json, Array<Track>::class.java).toList()
        }
        return historyList
    }

    override fun addTrackToHistory(track: TrackDto) {
        val trackId = searchHistoryList.indexOfFirst { it.trackId == track.trackId }
        if (trackId != -1) {
            searchHistoryList.removeAt(trackId)
            searchHistoryList.add(0, track.toTrack())
        } else {
            searchHistoryList.add(0, track.toTrack())
        }
        if (searchHistoryList.size > SEARCH_HISTORY_LIST_SIZE) {
            searchHistoryList.removeLast()
        }
        saveHistory(searchHistoryList)
    }

    private fun saveHistory(historyList: List<Track>) {
        sharedPrefs.edit()
            .putString(SEARCH_KEY, gson.toJson(historyList))
            .apply()
    }

    @SuppressLint("CommitPrefEdits")
    override fun clearHistory() {
        searchHistoryList.clear()
        sharedPrefs.edit().remove(SEARCH_KEY).apply()
    }

    companion object {
        private const val SEARCH_KEY = "key_for_search"
        private const val SEARCH_HISTORY_LIST_SIZE = 10
    }
}