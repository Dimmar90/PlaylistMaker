package com.practicum.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistSheetDialogBinding
import com.practicum.playlistmaker.media.ui.PlaylistState.StateTracksIds
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.search.ui.TracksAdapter
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class PlaylistSheetDialogFragment : BottomSheetDialogFragment(), TracksAdapter.TrackListener {

    private val viewModel: PlaylistsViewModel by viewModel()
    private val searchViewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistSheetDialogBinding
    private val tracksList = ArrayList<Track>()
    private lateinit var recyclerView: RecyclerView
    private val tracksAdapter = TracksAdapter(tracksList, this)
    private var id = 0

    override fun getTheme() = R.style.PlaylistBottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isHideable = false
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistSheetDialogBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("Arg", this) { _, bundle ->
            val playlistId: Int = bundle.getString("Arg")!!.toInt()
            id = playlistId
            viewModel.getTracksIds(playlistId)

            viewModel.observePlaylistsState().observe(viewLifecycleOwner) { playlistState ->
                putPlaylistsState(playlistState)
            }
        }
    }

    private fun putPlaylistsState(playlistState: PlaylistState) {
        if (playlistState is StateTracksIds) {
            tracksIdsState(playlistState.trackIds)
        }
        if (playlistState is PlaylistState.StateTracksList) {
            tracksListState(playlistState.tracks)
        }
    }

    private fun tracksIdsState(tracksIds: JSONArray) {
        if (tracksIds.length() == 0) {
            dismiss()
        }
        for (i in 0 until tracksIds.length()) {
            viewModel.searchDebounce(tracksIds.getInt(i).toString())
        }
    }

    private fun tracksListState(tracks: List<Track>) {
        var totalTime = 0
        tracksList.clear()
        tracksList.addAll(tracks)

        tracksList.forEach { track ->
            totalTime += track.trackTimeMillis
        }
        val totalPlaylistTime: String =
            SimpleDateFormat("mm", Locale.getDefault()).format(totalTime)

        val bundle = Bundle()
        bundle.putString("ArgTotalTime", totalPlaylistTime)
        parentFragmentManager.setFragmentResult("ArgTotalTime", bundle)

        val tracksAdapter = TracksAdapter(tracksList, this)
        recyclerView.adapter = tracksAdapter
    }

    override fun onTrackClick(track: Track) {
        searchViewModel.addTrackToHistory(track)
        findNavController().navigate(R.id.playerActivity)
        dismiss()
    }

    override fun onItemLongClick(track: Track): Boolean {
        setDialogBuilder(track)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDialogBuilder(track: Track) {
        val message = getString(R.string.delete_track_message)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(message)
            .setNegativeButton(R.string.no) { _, _ ->
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrack(track, id)
                tracksAdapter.notifyDataSetChanged()
            }
            .show()
    }
}