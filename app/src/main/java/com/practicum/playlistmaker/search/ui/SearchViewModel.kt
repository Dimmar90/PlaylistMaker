package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SearchViewModel(
    context: Context,
    private var searchTracksInteractor: SearchTracksInteractor,
    private var historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val tracksList: MutableList<Track> = ArrayList()

    private var searchJob: Job? = null
    private var searchText: String? = null

    private val renderState = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = renderState

    private var searchHistory = listOf<Track>()

    private var isNightModeOn = false

    init {
        isNightModeOn = checkNightModeOn(context)
        loadSearchHistory()
    }

    private fun loadSearchHistory() {
        searchHistory = historyInteractor.getHistory()
        visibilityOfHistory()
    }

    fun searchDebounce(editText: String) {
        this.searchText = editText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (isActive) {
                getTracksList(searchText.toString())
            }
            delay(SEARCH_DEBOUNCE_DELAY)
        }
    }

    private fun renderState(state: TracksState) {
        renderState.postValue(state)
    }

    private fun getTracksList(text: String) {

        if (text.isNotEmpty()) {
            renderState(TracksState.ShowLoading)
            viewModelScope.launch {
                searchTracksInteractor
                    .searchTracksList(text)
                    .collect { pair ->
                        tracksList.clear()
                        tracksList.addAll(pair.first)
                        when {
                            pair.second -> {
                                if (isNightModeOn) {
                                    renderState(
                                        TracksState.ShowConnectionError(
                                            image = R.drawable.connection_error_image_dark_mode,
                                            message = R.string.connection_error_message,
                                            extraMessage = R.string.extra_connection_error_message
                                        )
                                    )
                                } else {
                                    renderState(
                                        TracksState.ShowConnectionError(
                                            image = R.drawable.connection_error_image,
                                            message = R.string.connection_error_message,
                                            extraMessage = R.string.extra_connection_error_message
                                        )
                                    )
                                }
                            }

                            tracksList.isEmpty() -> {
                                if (isNightModeOn) {
                                    renderState(
                                        TracksState.ShowEmptySearch(
                                            image = R.drawable.empty_search_image_dark_mode,
                                            message = R.string.empty_search
                                        )
                                    )
                                } else {
                                    renderState(
                                        TracksState.ShowEmptySearch(
                                            image = R.drawable.empty_search_image,
                                            message = R.string.empty_search
                                        )
                                    )
                                }
                            }

                            else -> {
                                renderState(
                                    TracksState.ShowTracksList(
                                        tracksList = tracksList
                                    )
                                )
                            }
                        }
                    }
            }
        } else {
            loadSearchHistory()
            visibilityOfHistory()
        }
    }

    fun addTrackToHistory(track: Track) {
        historyInteractor.addTrackToHistory(track.toTrackDto())
    }

    fun visibilityOfHistory() {
        val historyList = historyInteractor.getHistory()
        if (historyList.isNotEmpty()) {
            renderState(TracksState.ShowHistory(historyList))
        } else {
            renderState(TracksState.ShowEmptyScreen)
        }
    }

    fun clearSearchHistory() {
        historyInteractor.clearHistory()
        loadSearchHistory()
    }

    private fun checkNightModeOn(context: Context): Boolean {
        var isNightModeOn = false
        when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> isNightModeOn = true
            Configuration.UI_MODE_NIGHT_NO -> isNightModeOn = false
        }
        return isNightModeOn
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}