package com.practicum.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment(), TracksAdapter.TrackListener {

    private val viewModel: FavoritesViewModel by viewModel()
    private var binding: FragmentFavoritesBinding? = null

    private val favoriteTracks = ArrayList<Track>()
    private lateinit var favoriteTracksRecycler: RecyclerView
    private val favoriteTracksAdapter = TracksAdapter(favoriteTracks, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        favoriteTracksRecycler = binding!!.favoriteTracksList
        favoriteTracksRecycler.layoutManager = LinearLayoutManager(requireContext())
        favoriteTracksRecycler.adapter = favoriteTracksAdapter

        viewModel.getFavoriteTracks()
        viewModel.observeFavoritesState().observe(viewLifecycleOwner) { favoritesState ->
            putFavoritesState(favoritesState)
        }
        return binding!!.root
    }

    private fun putFavoritesState(state: FavoritesState) {
        when (state) {
            is FavoritesState.StateEmpty -> emptyState()
            is FavoritesState.StateContent -> contentState(state.tracks)
        }
    }

    private fun emptyState() {
        favoriteTracksRecycler.isVisible = false
        binding!!.emptyMediaLayout.isVisible = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun contentState(tracks: List<Track>) {
        favoriteTracks.clear()
        binding!!.emptyMediaLayout.isVisible = false
        favoriteTracksRecycler.isVisible = true
        favoriteTracks.addAll(tracks)
        favoriteTracksAdapter.notifyDataSetChanged()
    }

    override fun onTrackClick(track: Track) {
        viewModel.addTrackToHistory(track)
        val intent = Intent(requireActivity(), PlayerActivity::class.java)
        putExtras(intent, track)
        startActivity(intent)
    }

    private fun putExtras(intent: Intent, track: Track) {
        intent.putExtra("trackId", track.trackId)
        intent.putExtra("trackName", track.trackName)
        intent.putExtra("artistName", track.artistName)
        intent.putExtra("trackTimeMillis", track.trackTimeMillis)
        intent.putExtra("artworkUrl100", track.artworkUrl100)
        intent.putExtra("collectionName", track.collectionName)
        intent.putExtra("releaseDate", track.releaseDate)
        intent.putExtra("primaryGenreName", track.primaryGenreName)
        intent.putExtra("country", track.country)
        intent.putExtra("previewUrl", track.previewUrl)
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}