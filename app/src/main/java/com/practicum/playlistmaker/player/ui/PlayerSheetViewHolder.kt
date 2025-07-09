package com.practicum.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.models.Playlist

open class PlayerSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val playlistCover: ImageView = itemView.findViewById(R.id.cover)
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
    private val playlistTracksAmount: TextView = itemView.findViewById(R.id.playlist_tracks_amount)

    fun bind(playlist: Playlist, playlistListener: PlayerSheetAdapter.PlayerSheetListener) {
        playlistName.text = playlist.playlistName
        playlistTracksAmount.text = playlist.tracksAmount.toString()

        Glide.with(itemView)
            .load(playlist.coverPath)
            .placeholder(R.drawable.placeholder_icon)
            .into(playlistCover)
        itemView.setOnClickListener {
            playlistListener.onPlaylistClick(playlist)
        }
    }
}