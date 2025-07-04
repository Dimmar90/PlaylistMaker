package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.ui.MainViewModel
import com.practicum.playlistmaker.media.ui.FavoritesViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        SearchViewModel(get(), get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel{
        FavoritesViewModel(get(), get())
    }
}