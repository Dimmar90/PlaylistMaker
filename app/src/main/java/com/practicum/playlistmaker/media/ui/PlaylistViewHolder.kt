package com.practicum.playlistmaker.media.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.models.Playlist

open class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val playlistCover: ImageView = itemView.findViewById(R.id.cover)
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
    private val playlistTracksAmount: TextView = itemView.findViewById(R.id.tracks_amount)

    fun bind(playlist: Playlist, playlistListener: PlaylistAdapter.PlaylistListener) {
        playlistName.text = playlist.playlistName
        playlistTracksAmount.text = formatCount(playlist.tracksAmount)

        Glide.with(itemView)
            .load(playlist.coverPath)
            .placeholder(R.drawable.placeholder_icon)
            .into(playlistCover)
        itemView.setOnClickListener {
            playlistListener.onPlaylistClick(playlist)
        }
    }

    private fun formatCount(tracksAmount: Int): String{
        val lastTwoDigits = tracksAmount % 100
        val lastDigit = tracksAmount % 10

        val suffix = when {
            lastTwoDigits in 11..14 -> "треков"
            lastDigit == 1 -> "трек"
            lastDigit in 2..4 -> "трека"
            else -> "треков"
        }
        return "$tracksAmount $suffix"
    }
}