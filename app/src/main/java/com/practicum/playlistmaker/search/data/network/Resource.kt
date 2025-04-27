package com.practicum.playlistmaker.search.data.network

sealed class Resource <T> (val data: T? = null, val isError: Boolean = true) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(isError : Boolean, data: T? = null): Resource<T>(data, isError)
}

