package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import java.util.Locale

open class TracksViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameView: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeView: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkView: ImageView = itemView.findViewById(R.id.cover)
    private var drawable: Drawable? = null

    fun bind(
        model: Track,
        trackListener: TracksAdapter.TrackListener,
    ) {
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)

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
            .load(model.artworkUrl100)
            .centerInside()
            .transform(RoundedCorners(8))
            .placeholder(drawable)
            .into(artworkView)
        itemView.setOnClickListener {
            trackListener.onTrackClick(model)
        }
        itemView.setOnLongClickListener {
            trackListener.onItemLongClick(model)
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
}