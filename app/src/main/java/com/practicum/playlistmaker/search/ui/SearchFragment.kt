package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TracksState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(), TracksAdapter.TrackListener {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModel()

    private val tracks = ArrayList<Track>()
    private lateinit var tracksRecycler: RecyclerView
    private val tracksAdapter = TracksAdapter(tracks, this)
    private lateinit var editText: EditText

    private lateinit var connectionImage: ImageView
    private lateinit var connectionMessage: TextView
    private lateinit var connectionExtraMessage: TextView
    private lateinit var updateButton: Button
    private lateinit var progressBar: ProgressBar

    private val searchHistoryList = ArrayList<Track>()
    private lateinit var historyRecycler: RecyclerView
    private val historyAdapter = TracksAdapter(searchHistoryList, this)
    private lateinit var searchHistoryLayout: RelativeLayout
    private lateinit var clearHistoryButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        val searchView = binding.searchBar
        editText =
            (searchView.findViewById(androidx.appcompat.R.id.search_src_text))

        tracksRecycler = binding.tracksList
        tracksRecycler.layoutManager = LinearLayoutManager(requireContext())
        tracksRecycler.adapter = tracksAdapter

        connectionImage = binding.connectionImage
        connectionMessage = binding.connectionText
        connectionExtraMessage = binding.extraConnectionText
        updateButton = binding.updateSearchButton
        progressBar = binding.progressBar

        searchHistoryLayout = binding.searchHistory
        historyRecycler = binding.searchHistoryList
        historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        historyRecycler.adapter = historyAdapter
        clearHistoryButton = binding.clearHistoryButton

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        editText.addTextChangedListener(
            beforeTextChanged = { _: CharSequence?, _, _, _ ->
            },
            onTextChanged = { s: CharSequence?, _, _, _ ->
                searchHistoryLayout.visibility = View.GONE
                viewModel.searchDebounce(editText.text.toString())

                if (editText.hasFocus() && s?.isEmpty() == true) {
                    viewModel.visibilityOfHistory()

                }
            },
            afterTextChanged = { _ -> }
        )

        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
            viewModel.visibilityOfHistory()
        }

        updateButton.setOnClickListener {
            viewModel.searchDebounce(editText.text.toString())
        }

        return binding.root
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.ShowLoading -> showLoading()
            is TracksState.ShowEmptyScreen -> showEmptyScreen()
            is TracksState.ShowHistory -> showSearchHistory(state.historyList)

            is TracksState.ShowTracksList -> getSearchingTracks(state.tracksList)
            is TracksState.ShowEmptySearch -> setEmptySearchParameters(state.image, state.message)
            is TracksState.ShowConnectionError -> setFailureConnectionParameters(
                state.image,
                state.message,
                state.extraMessage
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getSearchingTracks(tracksList: List<Track>) {
        tracksRecycler.isVisible = true
        connectionImage.isVisible = false
        connectionMessage.isVisible = false
        connectionExtraMessage.isVisible = false
        updateButton.isVisible = false
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = false

        tracks.clear()
        tracks.addAll(tracksList)
        tracksAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setEmptySearchParameters(image: Int, message: Int) {
        tracksRecycler.isVisible = false
        connectionImage.visibility = View.VISIBLE
        connectionMessage.visibility = View.VISIBLE
        connectionExtraMessage.visibility = View.GONE
        updateButton.visibility = View.GONE
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = false
        connectionImage.setImageResource(image)
        connectionMessage.setText(message)

        tracks.clear()
        tracksAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setFailureConnectionParameters(image: Int, message: Int, extraMessage: Int) {
        tracksRecycler.isVisible = false
        connectionImage.visibility = View.VISIBLE
        connectionMessage.visibility = View.VISIBLE
        connectionExtraMessage.visibility = View.VISIBLE
        updateButton.visibility = View.VISIBLE
        connectionImage.setImageResource(image)
        connectionMessage.setText(message)
        connectionExtraMessage.setText(extraMessage)
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = false

        tracks.clear()
        tracksAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        tracksRecycler.isVisible = false
        progressBar.isVisible = true
        searchHistoryLayout.isVisible = false
    }

    private fun showEmptyScreen() {
        connectionImage.isVisible = false
        connectionMessage.isVisible = false
        connectionExtraMessage.isVisible = false
        updateButton.isVisible = false
        progressBar.isVisible = false
        tracksRecycler.isVisible = false
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSearchHistory(historyList: List<Track>) {
        searchHistoryList.clear()
        searchHistoryList.addAll(historyList)
        historyAdapter.notifyDataSetChanged()
        connectionImage.isVisible = false
        connectionMessage.isVisible = false
        connectionExtraMessage.isVisible = false
        updateButton.isVisible = false
        progressBar.isVisible = false
        historyAdapter.notifyDataSetChanged()
        tracksRecycler.isVisible = false
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTrackClick(track: Track) {
        viewModel.addTrackToHistory(track)
        historyAdapter.notifyDataSetChanged()
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
}