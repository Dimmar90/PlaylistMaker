package com.practicum.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.search.ui.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class PlaylistFragment : Fragment(), TracksAdapter.TrackListener {

    private val viewModel: PlaylistsViewModel by viewModel()
    private val searchViewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var returnButton: ImageButton
    private lateinit var shareButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var playlistCover: ImageView
    private lateinit var playlistName: TextView
    private lateinit var playlistTotalTime: TextView
    private lateinit var playlistAmount: TextView
    private lateinit var playlistDescription: TextView

    private lateinit var playlistDialogCover: ImageView
    private lateinit var playlistDialogName: TextView
    private lateinit var playlistDialogAmount: TextView
    private lateinit var sharePlaylist: TextView
    private lateinit var editPlaylist: TextView
    private lateinit var deletePlaylist: TextView

    private lateinit var bottomSheetContainer: RelativeLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>

    private lateinit var bottomMenuSheetContainer: RelativeLayout
    private lateinit var bottomMenuSheetBehavior: BottomSheetBehavior<RelativeLayout>

    private val tracksList = ArrayList<Track>()
    private lateinit var recyclerView: RecyclerView
    private var playlistId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        playlistCover = binding.playlistArtwork
        playlistName = binding.playlistName
        playlistDescription = binding.playlistDescription
        playlistTotalTime = binding.playlistTime
        playlistAmount = binding.playlistAmount
        returnButton = binding.playlistReturnButton
        shareButton = binding.playlistShareButton
        menuButton = binding.playlistMenuButton

        playlistDialogName = binding.playlistDialogName
        playlistDialogCover = binding.cover
        playlistDialogAmount = binding.playlistTracksAmount
        sharePlaylist = binding.shareText
        editPlaylist = binding.editText
        deletePlaylist = binding.deleteText

        bottomSheetContainer = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        bottomMenuSheetContainer = binding.bottomMenuSheet
        bottomMenuSheetBehavior = BottomSheetBehavior.from(bottomMenuSheetContainer)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomMenuSheetBehavior.isHideable = true
        bottomMenuSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        playlistId = arguments?.getString("Argument")!!.toInt()
        viewModel.getPlaylistById(playlistId)

        viewModel.observePlaylistsState().observe(viewLifecycleOwner) { playlistState ->
            putPlaylistsState(playlistState, view)
        }

        returnToPlaylists()
    }

    private fun putPlaylistsState(playlistState: PlaylistState, view: View) {

        if (playlistState is PlaylistState.StateTracksList) {
            tracksListState(playlistState.tracks)
        }

        if (playlistState is PlaylistState.StatePlaylist) {
            playlistState(playlistState.playlist, view)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun tracksListState(tracks: List<Track>) {
        var totalTime = 0
        tracksList.clear()
        tracksList.addAll(tracks)

        tracksList.forEach { track ->
            totalTime += track.trackTimeMillis
        }
        val totalPlaylistTime: String =
            SimpleDateFormat("mm", Locale.getDefault()).format(totalTime)

        playlistTotalTime.text = "$totalPlaylistTime ${getText(R.string.minutes)}"
        playlistAmount.text = formatCount(tracks.size)

        val tracksAdapter = TracksAdapter(tracksList, this)
        recyclerView.adapter = tracksAdapter
    }

    @SuppressLint("UseRequireInsteadOfGet", "SetTextI18n")
    private fun playlistState(playlist: Playlist, view: View) {

        viewModel.getTracksIds(playlist.id!!)
        viewModel.getTracksList(playlist.id)

        Glide.with(view)
            .load(playlist.coverPath)
            .placeholder(R.drawable.placeholder_playlist)
            .into(playlistCover)

        Glide.with(view)
            .load(playlist.coverPath)
            .placeholder(R.drawable.placeholder_playlist)
            .into(playlistDialogCover)

        playlistName.text = playlist.playlistName
        playlistDialogName.text = playlist.playlistName
        playlistDescription.text = playlist.playlistDescription
        playlistAmount.text = formatCount(playlist.tracksAmount)
        playlistDialogAmount.text = formatCount(playlist.tracksAmount)

        if (playlist.tracksIds.length() > 0) {
            bottomSheetBehavior.isHideable = false
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            view.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
                    && bottomMenuSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN
                ) {
                    bottomSheetBehavior.isHideable = true
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    bottomSheetBehavior.isHideable = false
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                if (bottomMenuSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomMenuSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        } else {
            viewModel.showSnackBar(
                view,
                getText(R.string.share_empty_playlist_message).toString(), requireContext()
            )
        }

        menuButton.setOnClickListener {
            bottomMenuSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        shareButton.setOnClickListener {
            sharePlaylist(playlist)
        }
        sharePlaylist.setOnClickListener {
            sharePlaylist(playlist)
        }
        editPlaylist.setOnClickListener {
            editPlaylist(playlist)
        }
        deletePlaylist.setOnClickListener {
            deletePlaylist(playlist)
        }
    }

    private fun sharePlaylist(playlist: Playlist) {
        if (playlist.tracksIds.length() == 0) {
            val text =
                "${getText(R.string.share_empty_playlist_message)}"
            Toast.makeText(
                requireContext(),
                text,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val playlistNameMessage = "${playlist.playlistName} \n"
            val playlistDescriptionMessage = "${playlist.playlistDescription} \n"
            val amountMessage = formatCount(playlist.tracksAmount)
            var tracksListMessage = String()
            var num = 0
            tracksList.forEach { track ->
                num += 1
                tracksListMessage += "\n$num. ${track.artistName} - ${track.trackName} (${
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(track.trackTimeMillis)
                })"
            }

            val shareMessage =
                playlistNameMessage + playlistDescriptionMessage + amountMessage + tracksListMessage

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun editPlaylist(playlist: Playlist) {
        val bundle = Bundle()
        bundle.putString("Argument", playlist.id.toString())
        findNavController().navigate(R.id.playlistCreatorFragment, bundle)
    }

    private fun deletePlaylist(playlist: Playlist) {
        setDeletePlaylistDialogBuilder(playlist)
    }

    private fun returnToPlaylists() {
        returnButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onTrackClick(track: Track) {
        searchViewModel.addTrackToHistory(track)
        findNavController().navigate(R.id.playerActivity)
    }

    override fun onItemLongClick(track: Track): Boolean {
        setDeleteTrackDialogBuilder(track)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDeleteTrackDialogBuilder(track: Track) {
        val message = getString(R.string.delete_track_message)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(message)
            .setNegativeButton(R.string.no) { _, _ ->
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrackFromPlaylist(track, playlistId)
            }
            .show()
    }

    private fun setDeletePlaylistDialogBuilder(playlist: Playlist) {
        val message = "${getString(R.string.delete_playlist_message)} «${playlist.playlistName}»?"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(message)
            .setNegativeButton(R.string.no) { _, _ ->
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist(playlist.id!!)
                findNavController().navigateUp()
            }
            .show()
    }

    @SuppressLint("RestrictedApi")
    private fun formatCount(tracksAmount: Int): String {
        val lastTwoDigits = tracksAmount % 100
        val lastDigit = tracksAmount % 10

        val suffix = when {
            lastTwoDigits in 11..14 -> getString(R.string.tracks)
            lastDigit == 1 -> getString(R.string.track)
            lastDigit in 2..4 -> getString(R.string.tracks1)
            else -> getString(R.string.tracks)
        }
        return "$tracksAmount $suffix"
    }
}