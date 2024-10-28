package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class TracksViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameView: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeView: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkView: ImageView = itemView.findViewById(R.id.cover)

    fun bind(model: Track, trackListener: TracksAdapter.TrackListener) {
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .centerInside()
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.placeholder_icon)
            .into(artworkView)
        itemView.setOnClickListener {
            trackListener.onTrackClick(model)
        }
    }
}