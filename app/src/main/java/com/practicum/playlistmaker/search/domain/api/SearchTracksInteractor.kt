package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchTracksInteractor {

    fun searchTracksList(text: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(tracks: List<Track>?, isError: Boolean)
    }
}