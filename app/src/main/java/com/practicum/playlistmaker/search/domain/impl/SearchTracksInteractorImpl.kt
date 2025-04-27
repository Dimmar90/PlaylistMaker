package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.api.SearchTracksRepository

class SearchTracksInteractorImpl(private val repository: SearchTracksRepository) :
    SearchTracksInteractor {

    override fun searchTracksList(text: String, consumer: SearchTracksInteractor.TracksConsumer) {
       repository.searchTracksList(text, consumer)
    }
}