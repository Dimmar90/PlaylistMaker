import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistMenuBottomSheetBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.PlaylistSheetDialogFragment
import com.practicum.playlistmaker.media.ui.PlaylistState
import com.practicum.playlistmaker.media.ui.PlaylistState.StateTracksIds
import com.practicum.playlistmaker.media.ui.PlaylistsViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class PlaylistSheetMenuDialogFragment() : BottomSheetDialogFragment() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistMenuBottomSheetBinding
    private var playlistSheetDialogFragment = PlaylistSheetDialogFragment()
    private lateinit var playlistCover: ImageView
    private lateinit var playlistName: TextView
    private lateinit var playlistAmount: TextView
    private lateinit var sharePlaylist: TextView
    private lateinit var editPlaylist: TextView
    private lateinit var deletePlaylist: TextView
    private val tracksList = ArrayList<Track>()
    private var playlistId = 0

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistMenuBottomSheetBinding.inflate(inflater, container, false)
        playlistCover = binding.cover
        playlistName = binding.playlistName
        playlistAmount = binding.playlistTracksAmount
        sharePlaylist = binding.shareText
        editPlaylist = binding.editText
        deletePlaylist = binding.deleteText

        playlistId = arguments?.getString("Arg1")!!.toInt()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylistById(playlistId)
        viewModel.observePlaylistsState().observe(viewLifecycleOwner) { playlistState ->
            putPlaylistsState(playlistState, view)
        }
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
        putPlaylistExtras(playlist, view)
        sharePlaylist(playlist)
        editPlaylist(playlist)
        deletePlaylist(playlist)
    }

    private fun putPlaylistExtras(playlist: Playlist, view: View) {
        Glide.with(view)
            .load(playlist.coverPath)
            .placeholder(R.drawable.placeholder_icon)
            .into(playlistCover)

        playlistName.text = playlist.playlistName
        playlistAmount.text = formatCount(playlist.tracksAmount)
        viewModel.getTracksIds(playlist.id!!)
    }

    private fun sharePlaylist(playlist: Playlist) {
        sharePlaylist.setOnClickListener {
            dismiss()
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

    private fun editPlaylist(playlist: Playlist) {
        editPlaylist.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Argument", playlist.id.toString())
            findNavController().navigate(R.id.playlistCreatorFragment, bundle)
        }
    }

    private fun deletePlaylist(playlist: Playlist) {
        deletePlaylist.setOnClickListener {
            setDialogBuilder(playlist)
        }
    }

    private fun setDialogBuilder(playlist: Playlist) {
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