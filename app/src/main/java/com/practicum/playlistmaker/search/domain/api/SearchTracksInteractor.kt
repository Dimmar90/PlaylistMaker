package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchTracksInteractor {
    fun searchTracksList(text: String): Flow<Pair<ArrayList<Track>, Boolean>>
}