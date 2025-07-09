package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBottomSheetBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.PlaylistState
import com.practicum.playlistmaker.media.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerSheet : BottomSheetDialogFragment(), PlayerSheetAdapter.PlayerSheetListener {

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistBottomSheetBinding
    private lateinit var addPlaylistButton: MaterialButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBottomSheetBinding.inflate(inflater, container, false)
//        addPlaylistButton = binding.addNewPlaylistButton
//        recyclerView = binding.recyclerView
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        viewModel.getPlaylists()
//        viewModel.observePlaylistsState().observe(viewLifecycleOwner) { playlistState ->
//            putPlaylistsState(playlistState)
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addPlaylistButton = binding.addNewPlaylistButton
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getPlaylists()
        viewModel.observePlaylistsState().observe(viewLifecycleOwner) { playlistState ->
            putPlaylistsState(playlistState)
        }

        addPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerSheet_to_playlistCreatorFragment)
        }
    }

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    private fun putPlaylistsState(playlistState: PlaylistState) {
        when (playlistState) {
            is PlaylistState.StateEmpty -> emptyState()
            is PlaylistState.StateContent -> contentState(playlistState.playlists)
        }
    }

    private fun emptyState() {

    }

    private fun contentState(playlists: List<Playlist>) {
        val playlistAdapter = PlayerSheetAdapter(playlists, this)
        recyclerView.adapter = playlistAdapter
    }

    override fun onPlaylistClick(playlist: Playlist) {

    }
}