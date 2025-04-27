package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.api.SearchTracksRepository
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.domain.impl.SearchTracksInteractorImpl
import com.practicum.playlistmaker.search.data.impl.SearchTracksRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl

object Creator {

//    private const val SEARCH_HISTORY = "search_activity_preferences"
//    private const val SETTINGS = "settings"

//    private fun getSearchTracksRepository(
//        networkClient: NetworkClient
//    ): SearchTracksRepository {
//        return SearchTracksRepositoryImpl(networkClient)
//    }
//
//    fun provideSearchTracksInteractor(
//        networkClient: NetworkClient
//    ): SearchTracksInteractor {
//        val repository = getSearchTracksRepository(networkClient)
//        return SearchTracksInteractorImpl(repository)
//    }

//    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
//        val sharedPrefs: SharedPreferences =
//            context.getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)
//        return SearchHistoryRepositoryImpl(sharedPrefs)
//    }
//
//    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
//        val repository = getSearchHistoryRepository(context)
//        return SearchHistoryInteractorImpl(repository)
//    }

//    private fun getPlayerRepository(): PlayerRepository {
//        return PlayerRepositoryImpl()
//    }
//
//    fun providePlayerInteractor(): PlayerInteractor {
//        val repository = getPlayerRepository()
//        return PlayerInteractorImpl(repository)
//    }

//    private fun getSettingsRepository(context: Context): SettingsRepository {
//        val sharedPrefs: SharedPreferences =
//            context.getSharedPreferences(SETTINGS, MODE_PRIVATE)
//        return SettingsRepositoryImpl(sharedPrefs)
//    }
//
//    fun provideSettingsInteractor(context: Context): SettingsInteractor {
//        val repository = getSettingsRepository(context)
//        return SettingsInteractorImpl(repository)
//    }
}