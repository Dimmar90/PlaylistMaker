package com.practicum.playlistmaker

import android.content.SharedPreferences
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class SearchHistory(
    private val sharedPrefs: SharedPreferences,
    private val historyMap: LinkedHashMap<String, String>,
    private val searchHistoryList: ArrayList<Track>,
    private val historyAdapter: TracksAdapter,
    private val adapter: TracksAdapter,
    private val gson: Gson,
    private val searchHistoryListRecycler: RecyclerView
) : AppCompatActivity() {

    fun onTrackClick(track: Track) {
        val trackGson = Gson().toJson(track)

        if (historyMap.keys.contains(track.trackId)) {
            historyMap.remove(track.trackId)
            searchHistoryList.remove(track)
        }

        if (historyMap.keys.size == 10) {
            historyMap.remove(historyMap.keys.first())
            searchHistoryList.removeAt(9)
        }

        historyMap[track.trackId] = trackGson
        searchHistoryList.add(0, track)

        sharedPrefs.edit()
            .putString(SEARCH_KEY, historyMap.values.toString())
            .apply()

        searchHistoryListRecycler.layoutManager = LinearLayoutManager(this)
        searchHistoryListRecycler.adapter = historyAdapter
    }

    fun addSearchHistory(
        sharedPrefs: SharedPreferences,
        searchHistoryListRecycler: RecyclerView
    ) {
        val json = sharedPrefs.getString(SEARCH_KEY, "")
        val historyList: List<Track> = gson.fromJson(json, Array<Track>::class.java).toList()
        searchHistoryList.addAll(historyList.reversed())

        for (track in historyList) {
            historyMap[track.trackId] = Gson().toJson(track)
        }

        searchHistoryListRecycler.layoutManager = LinearLayoutManager(this)
        searchHistoryListRecycler.adapter = historyAdapter
    }

    fun clearSearchHistory(
        sharedPrefs: SharedPreferences,
        historyRecycler: RecyclerView,
        searchHistory: RelativeLayout
    ) {
        sharedPrefs.edit().clear().apply()
        searchHistoryList.clear()
        historyMap.clear()
        historyRecycler.adapter = adapter
        searchHistory.visibility = View.GONE
    }
}