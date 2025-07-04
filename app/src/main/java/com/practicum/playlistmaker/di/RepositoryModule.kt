package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.data.impl.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.media.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.SearchTracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.SearchTracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }

    single<SearchTracksRepository> { SearchTracksRepositoryImpl(get()) }

    single<PlayerRepository> { PlayerRepositoryImpl() }

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    factory<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }
}