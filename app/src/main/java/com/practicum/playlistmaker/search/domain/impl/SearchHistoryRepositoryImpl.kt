package com.practicum.playlistmaker.search.domain.impl

import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository

const val SEARCH_KEY = "key_for_search"

class SearchHistoryRepositoryImpl(private val sharedPrefs: SharedPreferences) :
    SearchHistoryRepository {

    private val builder = GsonBuilder()
    private val gson = builder.create()
    private val searchHistoryList = mutableListOf<TrackDto>()

    override fun getHistory(searchHistoryListRecycler: RecyclerView): MutableList<TrackDto> {

        if (sharedPrefs.getString(SEARCH_KEY, "").toString().isNotEmpty()) {
            val json = sharedPrefs.getString(SEARCH_KEY, "")
            val historyList: List<TrackDto> = gson.fromJson(json, Array<TrackDto>::class.java).toList()
            searchHistoryList.addAll(historyList.reversed())
        }
        return searchHistoryList
    }

    override fun saveHistory(historyMap: MutableMap<String, String>) {

        sharedPrefs.edit()
            .putString(SEARCH_KEY, historyMap.values.toString())
            .apply()
    }

    override fun clearHistory() {

        sharedPrefs.edit().clear().apply()
    }
}