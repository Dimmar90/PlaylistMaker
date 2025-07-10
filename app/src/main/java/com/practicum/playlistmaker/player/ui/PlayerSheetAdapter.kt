package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.models.Playlist

class PlayerSheetAdapter(
    private var playlists: List<Playlist>,
    private val playlistListener: PlayerSheetListener
) : RecyclerView.Adapter<PlayerSheetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerSheetViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sheet_playlist_view, parent, false)
        return PlayerSheetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlayerSheetViewHolder, position: Int) {
        holder.bind(playlists[position], playlistListener)
    }

    interface PlayerSheetListener {
         fun onPlaylistClick(playlist: Playlist)
    }
}