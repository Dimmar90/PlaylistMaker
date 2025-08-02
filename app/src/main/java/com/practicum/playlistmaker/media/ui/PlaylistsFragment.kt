package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(), PlaylistAdapter.PlaylistListener {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistsBinding
    private lateinit var addPlaylistButton: Button
    private lateinit var emptyPlaylistImage: ImageView
    private lateinit var emptyPlaylistMessage: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        addPlaylistButton = binding.playlistsButton
        emptyPlaylistImage = binding.emptyPlaylistImage
        emptyPlaylistMessage = binding.emptyPlaylistMessage
        recyclerView = binding.recyclerView

        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        addPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_playlistCreatorFragment)
        }

        viewModel.getPlaylists()

        viewModel.observePlaylistsState().observe(viewLifecycleOwner) { playlistState ->
            putPlaylistsState(playlistState)
        }
        return binding.root
    }

    private fun putPlaylistsState(playlistState: PlaylistState) {
        when (playlistState) {
            is PlaylistState.StateEmpty -> emptyState()
            is PlaylistState.StateContent -> contentState(playlistState.playlists)
        }
    }

    private fun emptyState() {
        emptyPlaylistImage.isVisible = true
        emptyPlaylistMessage.isVisible = true
    }

    private fun contentState(playlists: List<Playlist>) {
        emptyPlaylistImage.isVisible = false
        emptyPlaylistMessage.isVisible = false
        val playlistAdapter = PlaylistAdapter(playlists, this)
        recyclerView.adapter = playlistAdapter
    }

    override fun onPlaylistClick(playlist: Playlist) {
    }
}