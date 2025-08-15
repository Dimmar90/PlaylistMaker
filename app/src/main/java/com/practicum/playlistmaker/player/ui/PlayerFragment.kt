package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {
    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var playButton: MaterialButton
    private lateinit var playerTime: TextView
    private lateinit var addToFavoritesButton: MaterialButton
    private lateinit var addToPlaylistButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playButton = binding.playTrackButton
        playerTime = binding.playerTime
        addToFavoritesButton = binding.addToFavoritesButton
        addToPlaylistButton = binding.addToMediaButton
        val returnButton = binding.playerReturnButton

        val trackCover = binding.trackArtwork
        val trackName = binding.songTitle
        val artistName = binding.artistName
        val trackDuration = binding.trackDuration
        val collectionName = binding.trackCollectionName
        val trackReleaseDate = binding.trackReleaseDate
        val trackGenre = binding.trackPrimaryGenreName
        val trackCountry = binding.trackCountry

        viewModel.addTrackCover(trackCover)

        viewModel.addTrackTitles(
            trackName,
            artistName,
            trackDuration,
            collectionName,
            trackReleaseDate,
            trackGenre,
            trackCountry
        )

        viewModel.observePlayerState().observe(viewLifecycleOwner) { playerState ->
            putPlayerState(playerState)
        }

        addToPlaylistButton.setOnClickListener {
            PlayerSheetDialogFragment().show(childFragmentManager, "newPlayerSheet")
        }

        returnButton.setOnClickListener {
            viewModel.pausePlayer()
            viewModel.resetPlayer()
            findNavController().navigateUp()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.pausePlayer()
                viewModel.resetPlayer()
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun putPlayerState(state: PlayerState) {
        when (state) {
            is PlayerState.StateDefault -> defaultState(state.isTrackFavorite)
            is PlayerState.StatePlaying -> playState(state.playbackTime)
            is PlayerState.StatePaused -> pauseState()
            is PlayerState.StateTrackEnded -> trackIsEndedState()
        }
    }

    private fun defaultState(trackFavorite: Boolean) {
        likeButtonFunctional(trackFavorite)
        playButton.setOnClickListener {
            playButton.setIconResource(R.drawable.pause_button)
            viewModel.playbackControl()
        }
    }

    private fun playState(playbackTime: String) {
        playerTime.text = playbackTime
        playButton.setOnClickListener {
            playButton.setIconResource(R.drawable.pause_button)
            viewModel.playbackControl()
        }
    }

    private fun pauseState() {
        playButton.setIconResource(R.drawable.play_track_button_icon)
    }

    @SuppressLint("SetTextI18n")
    private fun trackIsEndedState() {
        playerTime.text = "00:00"
        playButton.setIconResource(R.drawable.play_track_button_icon)
    }

    private fun likeButtonFunctional(isTrackFavorite: Boolean) {
        if (isTrackFavorite) {
            pushLikeButton()
        } else {
            releaseLikeButton()
        }

        addToFavoritesButton.setOnClickListener {
            if (isTrackFavorite) {
                releaseLikeButton()
                viewModel.deleteTrackFromFavorites()
            } else {
                pushLikeButton()
                viewModel.addTrackToFavorites()
            }
        }
    }

    private fun pushLikeButton() {
        addToFavoritesButton.setIconResource(R.drawable.add_like_button_icon_red)
        addToFavoritesButton.setIconTintResource(R.color.add_like_button)
    }

    private fun releaseLikeButton() {
        addToFavoritesButton.setIconResource(R.drawable.add_like_button_icon)
        addToFavoritesButton.setIconTintResource(R.color.white)
    }
}