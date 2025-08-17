package com.practicum.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.models.Playlist

open class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val playlistCover: ImageView = itemView.findViewById(R.id.cover)
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
    private val playlistTracksAmount: TextView = itemView.findViewById(R.id.tracks_amount)
    private var drawable: Drawable? = null

    fun bind(playlist: Playlist, playlistListener: PlaylistAdapter.PlaylistListener) {
        playlistName.text = playlist.playlistName
        playlistTracksAmount.text = formatCount(playlist.tracksAmount)

        drawable = if (checkNightModeOn(itemView.context)) {
            ResourcesCompat.getDrawable(
                itemView.context.resources,
                R.drawable.placeholder_dark,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                itemView.context.resources,
                R.drawable.placeholder,
                null
            )
        }

        Glide.with(itemView)
            .load(playlist.coverPath)
            .placeholder(drawable)
            .into(playlistCover)
        itemView.setOnClickListener {
            playlistListener.onPlaylistClick(playlist)
        }
    }

    private fun checkNightModeOn(context: Context): Boolean {
        var isNightModeOn = false
        when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> isNightModeOn = true
            Configuration.UI_MODE_NIGHT_NO -> isNightModeOn = false
        }
        return isNightModeOn
    }

    @SuppressLint("RestrictedApi")
    private fun formatCount(tracksAmount: Int): String {
        val lastTwoDigits = tracksAmount % 100
        val lastDigit = tracksAmount % 10

        val suffix = when {
            lastTwoDigits in 11..14 -> itemView.context.getString(R.string.tracks)
            lastDigit == 1 -> itemView.context.getString(R.string.track)
            lastDigit in 2..4 -> itemView.context.getString(R.string.tracks1)
            else -> itemView.context.getString(R.string.tracks)
        }
        return "$tracksAmount $suffix"
    }
}