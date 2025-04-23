package com.practicum.playlistmaker.main.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.media.ui.MediaActivity
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity

class MainViewModel(context: Context) : ViewModel() {

    private val settingsInteractor = Creator.provideSettingsInteractor(context)

    private val searchIntent = Intent(context, SearchActivity::class.java)
    private val mediaIntent = Intent(context, MediaActivity::class.java)
    private val settingsIntent = Intent(context, SettingsActivity::class.java)

    private val switchState = MutableLiveData<Boolean>()
    fun observeSwitchState(): LiveData<Boolean> = switchState

    init {
        switchState.value = getSwitchState()
    }

    private fun getSwitchState(): Boolean {
        return settingsInteractor.loadThemeSwitcher()
    }

    fun setTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (switchState.value == true) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun startSearch(context: Context) {
        startActivity(context, searchIntent, null)
    }

    fun startMedia(context: Context) {
        startActivity(context, mediaIntent, null)
    }

    fun startSettings(context: Context) {
        startActivity(context, settingsIntent, null)
    }

    companion object {
        fun getViewModelFactory(
            context: Context
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel(context)
            }
        }
    }
}