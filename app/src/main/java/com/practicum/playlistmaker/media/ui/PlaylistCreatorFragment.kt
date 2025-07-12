package com.practicum.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.net.Uri
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
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException

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
                coverPath = Uri.fromFile(File(saveCoverToStorage(it))).toString()
                Glide.with(this)
                    .load(uri)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(8)
                    )
                    .into(binding.playlistCover)
            }
        }

    private fun saveCoverToStorage(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val fileName = "playlist_${System.currentTimeMillis()}.jpg"
            val file = File(requireContext().filesDir, fileName)
            file.outputStream().use { output ->
                inputStream.use { input ->
                    input.copyTo(output)
                }
            }
            file.absolutePath

        } catch (e: IOException) {
            e.printStackTrace()
            null
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
                if (s?.isNotBlank() == true) {
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
        playlistTracksIds: JSONArray
    ): Playlist {
        return Playlist(null, playlistName, playlistDescription, coverPath, playlistTracksIds, 0)
    }

    private fun addPlaylist() {
        val playlistTracksIds = JSONArray()
        viewModel.addPlaylist(
            createPlaylist(
                playlistName.text.toString(),
                playlistDescription.text.toString(),
                coverPath,
                playlistTracksIds
            )
        )
    }

    @SuppressLint("ResourceAsColor")
    private fun activateCreateButton(view: View) {
        addPlaylistButton.setBackgroundColor(R.color.addPlaylistButton_color)
        addPlaylistButton.setOnClickListener {
            addPlaylist()

            findNavController().popBackStack()

            val text =
                "${getText(R.string.playlist)} ${playlistName.text} ${getText(R.string.create)}"
            viewModel.showSnackBar(view, text, requireContext())
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun deactivateCreateButton() {
        addPlaylistButton.setBackgroundColor(R.color.addPlaylistButton_diactivate_color)
    }

    private fun setDialogBuilder() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title)
            .setMessage(R.string.dialog_message)
            .setNeutralButton(R.string.dialog_neutral_button) { _, _ ->
            }
            .setPositiveButton(R.string.dialog_positive_button) { _, _ ->
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