package com.practicum.playlistmaker.player.ui

sealed class PlayerState {
    data object StateDefault : PlayerState()
    data class StatePlaying(val playbackTime: String) : PlayerState()
    data object StatePaused : PlayerState()
    data object StateTrackEnded : PlayerState()
}