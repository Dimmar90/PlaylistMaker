package com.practicum.playlistmaker.media.ui

import PlaylistSheetMenuDialogFragment
import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.PlaylistState.StateTracksIds
import com.practicum.playlistmaker.search.domain.models.Track
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

@Suppress("NAME_SHADOWING")
class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private var playlistSheetDialogFragment = PlaylistSheetDialogFragment()
    private var playlistMenuBottomSheet = PlaylistSheetMenuDialogFragment()
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var returnButton: ImageButton
    private lateinit var shareButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var playlistCover: ImageView
    private lateinit var playlistName: TextView
    private lateinit var playlistTotalTime: TextView
    private lateinit var playlistAmount: TextView
    private lateinit var playlistDescription: TextView
    private val tracksList = ArrayList<Track>()

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val playlistId: Int = arguments?.getString("Argument")!!.toInt()
            viewModel.getPlaylistById(playlistId)
        } catch (e: NullPointerException) {

        }

        viewModel.observePlaylistsState().observe(viewLifecycleOwner) { playlistState ->
            putPlaylistsState(playlistState, view)
        }

        returnToPlaylists()
    }

    private fun putPlaylistsState(playlistState: PlaylistState, view: View) {
        if (playlistState is StateTracksIds) {
            tracksIdsState(playlistState.trackIds)
        }

        if (playlistState is PlaylistState.StateTracksList) {
            tracksListState(playlistState.tracks)
        }

        if (playlistState is PlaylistState.StatePlaylist) {
            playlistState(playlistState.playlist, view)
        }
    }

    private fun tracksIdsState(tracksIds: JSONArray) {
        for (i in 0 until tracksIds.length()) {
            viewModel.searchDebounce(tracksIds.getInt(i).toString())
        }
    }

    private fun tracksListState(tracks: List<Track>) {
        tracksList.clear()
        tracksList.addAll(tracks)
    }

    @SuppressLint("UseRequireInsteadOfGet", "SetTextI18n")
    private fun playlistState(playlist: Playlist, view: View) {
        val bundle = Bundle()
        bundle.putString("Arg", playlist.id.toString())
        childFragmentManager.setFragmentResult("Arg", bundle)
        viewModel.getTracksIds(playlist.id!!)

        putPlaylistExtras(playlist, view)

        if (playlist.tracksIds.length() > 0) {
            playlistSheetDialogFragment.show(childFragmentManager, "newPlaylistSheet")
        }

        sharePlaylist(playlist)
        menuPlaylist(playlist)
    }

    private fun putPlaylistExtras(playlist: Playlist, view: View) {
        Glide.with(view)
            .load(playlist.coverPath)
            .placeholder(R.drawable.placeholder_icon)
            .into(playlistCover)

        playlistName.text = playlist.playlistName
        playlistDescription.text = playlist.playlistDescription

        childFragmentManager.setFragmentResultListener("ArgTotalTime", this) { _, bundle ->
            val totalTime: String = bundle.getString("ArgTotalTime")!!
            playlistTotalTime.text = "$totalTime ${getText(R.string.minutes)}"
        }

        playlistAmount.text = formatCount(playlist.tracksAmount)
    }

    private fun sharePlaylist(playlist: Playlist) {
        shareButton.setOnClickListener {
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
    }

    private fun menuPlaylist(playlist: Playlist) {
        menuButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Arg1", playlist.id.toString())
            playlistMenuBottomSheet.arguments = bundle
            playlistMenuBottomSheet.show(childFragmentManager, "newMenuPlaylistSheet")
        }
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

    private fun returnToPlaylists() {
        returnButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}