package com.practicum.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreatorFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistCreatorBinding
    private lateinit var addPlaylistButton: Button
    private lateinit var returnButton: ImageButton
    private lateinit var playlistName: EditText
    private lateinit var playlistDescription: EditText
    private lateinit var playlistCover: ImageView
    private var coverPath: String = ""

    private val pickCover =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                coverPath = it.toString()
                Glide.with(this)
                    .load(uri)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(8)
                    )
                    .into(binding.playlistCover)
            }
        }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
        addPlaylistButton = binding.createPlaylistButton
        returnButton = binding.returnButtonFromPlaylist
        playlistName = binding.playlistNameText
        playlistDescription = binding.playlistDescriptionText
        playlistCover = binding.playlistCover

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistCover.setOnClickListener {
            pickCover.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        playlistName.addTextChangedListener(
            onTextChanged = { s: CharSequence?, _, _, _ ->
                if (s?.isEmpty() == false) {
                    activateCreateButton(view)
                } else {
                    deactivateCreateButton()
                }
            }
        )

        returnButton.setOnClickListener {
            returnFunctional()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                returnFunctional()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        coverPath: String,
        playlistTracks: MutableList<Track>
    ): Playlist {
        return Playlist(playlistName, playlistDescription, coverPath, playlistTracks, 0)
    }

    private fun addPlaylist() {
        val playlistTracks: MutableList<Track> = mutableListOf()
        viewModel.addPlaylist(
            createPlaylist(
                playlistName.text.toString(),
                playlistDescription.text.toString(),
                coverPath,
                playlistTracks
            )
        )
    }

    private fun activateCreateButton(view: View) {
        addPlaylistButton.setBackgroundColor(Color.parseColor("#3772E7"))
        addPlaylistButton.setOnClickListener {
            addPlaylist()

            findNavController().popBackStack()

            val text = "Плейлист ${playlistName.text} создан"
            viewModel.showSnackBar(view, text, requireContext())
        }
    }

    private fun deactivateCreateButton() {
        addPlaylistButton.setBackgroundColor(Color.parseColor("#AEAFB4"))
    }

    private fun setDialogBuilder() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title)
            .setMessage(R.string.dialog_message)
            .setNeutralButton(R.string.dialog_neutral_button) { dialog, which ->
            }
            .setPositiveButton(R.string.dialog_positive_button) { dialog, which ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun returnFunctional() {
        if (playlistName.text.isNotEmpty()
            || playlistDescription.text.isNotEmpty()
            || coverPath.isNotEmpty()
        ) {
            setDialogBuilder()
        } else {
            findNavController().navigateUp()
        }
    }
}