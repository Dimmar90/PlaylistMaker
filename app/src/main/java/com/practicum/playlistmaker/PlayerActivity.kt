package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track: Track = intent.getParcelableExtra("track")!!

        returnToMedia()
        getTrackCover(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
        addTrackTitles(track)
        playTrack()
        addToMedia()
    }

    private fun returnToMedia() {
        val returnButton = findViewById<ImageButton>(R.id.player_return_button)
        returnButton.setOnClickListener {
            finish()
        }
    }

    private fun getTrackCover(url: String) {
        val trackCover = findViewById<ImageView>(R.id.track_artwork)
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.placeholder_icon)
            .centerInside()
            .transform(RoundedCorners(8))
            .into(trackCover)
    }

    private fun addTrackTitles(track: Track) {
        val songTitle = findViewById<TextView>(R.id.song_title)
        songTitle.text = track.trackName

        val artistName = findViewById<TextView>(R.id.artist_name)
        artistName.text = track.artistName

        val trackDuration = findViewById<TextView>(R.id.track_duration)
        trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        val collectionName = findViewById<TextView>(R.id.track_collectionName)
        collectionName.text = track.collectionName

        val releaseDate = findViewById<TextView>(R.id.track_releaseDate)
        releaseDate.text = track.releaseDate.take(4)

        val primaryGenreName = findViewById<TextView>(R.id.track_primaryGenreName)
        primaryGenreName.text = track.primaryGenreName

        val country = findViewById<TextView>(R.id.track_country)
        country.text = track.country
    }

    private fun playTrack() {
        val playTrackButton = findViewById<MaterialButton>(R.id.play_track_button)
        playTrackButton.setOnClickListener {
            Toast.makeText(this, "Play Track", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToMedia() {
        val addToMediaButton = findViewById<MaterialButton>(R.id.add_to_media_button)
        addToMediaButton.setOnClickListener {
            Toast.makeText(this, "Add Track To Media", Toast.LENGTH_SHORT).show()
        }
    }
}