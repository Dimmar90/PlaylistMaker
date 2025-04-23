package com.practicum.playlistmaker.settings.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.main.ui.MainActivity

class SettingsViewModel(context: Context) : ViewModel() {

    private val settingsInteractor = Creator.provideSettingsInteractor(context)

    private val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message))
        type = "text/plain"
    }
    private val shareIntent = Intent.createChooser(sendIntent, null)
    private val supportIntent = Intent(Intent.ACTION_SENDTO)
    private val offerIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.offer)))
    private val mainIntent = Intent(context, MainActivity::class.java)

    private val switchState = MutableLiveData<Boolean>()
    fun observeSwitchState(): LiveData<Boolean> = switchState

    init {
        switchState.value = getSwitchState()
    }

    fun putSwitchState(checked: Boolean) {
        settingsInteractor.saveThemeSwitcher(checked)
        switchState.value = checked
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

    fun sharing(context: Context) {
        startActivity(context, shareIntent, null)
    }

    fun support(context: Context) {
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(context.getString(R.string.support_mail))
        )
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_subject))
        supportIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_message))
        startActivity(context, supportIntent, null)
    }

    fun termsOfUse(context: Context) {
        startActivity(context, offerIntent, null)
    }

    fun returnToMain(context: Context) {
        startActivity(context, mainIntent, null)
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(context)
            }
        }
    }
}