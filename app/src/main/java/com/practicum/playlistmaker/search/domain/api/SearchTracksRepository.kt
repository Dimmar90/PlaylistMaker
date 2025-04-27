package com.practicum.playlistmaker.search.domain.api

interface SearchTracksRepository {
    fun searchTracksList(text: String, consumer: SearchTracksInteractor.TracksConsumer)
}