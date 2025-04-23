package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.data.network.Resource
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.api.SearchTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import java.util.concurrent.Executors

class SearchTracksRepositoryImpl(
    private val networkClient: NetworkClient,
) : SearchTracksRepository {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracksList(text: String, consumer: SearchTracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = searchTracksListRepo(text)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, false)
                }

                is Resource.Error -> {
                    consumer.consume(null, true)
                }
            }
        }
    }

    private fun searchTracksListRepo(text: String): Resource<List<Track>> {

        val responce = networkClient.doRequest(TracksSearchRequest(text))

        return when (responce.resultCode) {
            200 -> {
                Resource.Success(((responce as TracksSearchResponse).results.map { trackDto -> trackDto.toTrack() }))
            }

            0 -> {
                Resource.Error(true)
            }

            else -> {
                Resource.Error(true)
            }
        }
    }
}