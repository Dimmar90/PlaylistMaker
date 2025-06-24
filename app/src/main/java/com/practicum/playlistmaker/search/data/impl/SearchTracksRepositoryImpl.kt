package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTracksRepositoryImpl(
    private val networkClient: NetworkClient,
) : SearchTracksRepository {

    override fun searchTracksList(text: String): Flow<Pair<ArrayList<Track>, Boolean>> = flow {
        val responce = networkClient.doRequest(TracksSearchRequest(text))
        when (responce.resultCode) {
            200 -> {
                val tracks = (responce as TracksSearchResponse).results
                val data = tracks.map { trackDto -> trackDto.toTrack() }
                emit(Pair(data, false) as Pair<ArrayList<Track>, Boolean>)
            }
            0 -> {
                emit(Pair(null, true) as Pair<ArrayList<Track>, Boolean>)
            }
            else -> {
                emit(Pair(null, true) as Pair<ArrayList<Track>, Boolean>)
            }
        }
    }
}