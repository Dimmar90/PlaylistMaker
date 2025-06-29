package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.network.ItunesApi
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    single<NetworkClient> { RetrofitNetworkClient(get(), get()) }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}