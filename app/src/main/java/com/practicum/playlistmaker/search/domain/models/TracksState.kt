package com.practicum.playlistmaker.search.domain.models

sealed interface TracksState {

    data object ShowLoading : TracksState

    data object ShowEmptyScreen : TracksState

    data object ShowHistory : TracksState

    data class ShowTracksList(
        val tracksList: List<Track>
    ) : TracksState

    data class ShowEmptySearch(
        val image: Int, val message: Int
    ) : TracksState

    data class ShowConnectionError(
        val image: Int, val message: Int, val extraMessage: Int
    ) : TracksState
}