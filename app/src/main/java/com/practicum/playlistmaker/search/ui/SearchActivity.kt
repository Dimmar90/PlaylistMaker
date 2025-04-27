package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.main.ui.MainActivity
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TracksState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity(), TracksAdapter.TrackListener {
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: ActivitySearchBinding

    private val tracks = ArrayList<Track>()
    private lateinit var tracksRecycler: RecyclerView
    private val tracksAdapter = TracksAdapter(tracks, this)
    private lateinit var editText: EditText

    private lateinit var connectionImage: ImageView
    private lateinit var connectionMessage: TextView
    private lateinit var connectionExtraMessage: TextView
    private lateinit var returnButton: MaterialButton
    private lateinit var updateButton: Button
    private lateinit var progressBar: ProgressBar

    private val searchHistoryList = ArrayList<Track>()
    private lateinit var historyRecycler: RecyclerView
    private val historyAdapter = TracksAdapter(searchHistoryList, this)
    private lateinit var searchHistoryLayout: RelativeLayout
    private lateinit var clearHistoryButton: Button

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchView = binding.searchBar
        editText =
            (searchView.findViewById(androidx.appcompat.R.id.search_src_text))

        tracksRecycler = binding.tracksList
        tracksRecycler.layoutManager = LinearLayoutManager(this)
        tracksRecycler.adapter = tracksAdapter

        connectionImage = binding.connectionImage
        connectionMessage = binding.connectionText
        connectionExtraMessage = binding.extraConnectionText
        returnButton = binding.searchReturnButton
        updateButton = binding.updateSearchButton
        progressBar = binding.progressBar

        searchHistoryLayout = binding.searchHistory
        historyRecycler = binding.searchHistoryList
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter
        clearHistoryButton = binding.clearHistoryButton
        val mainIntent = Intent(application.applicationContext, MainActivity::class.java)

        viewModel.observeState().observe(this) { state ->
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(mainIntent)
                finish()
            }
        })

        returnButton.setOnClickListener {
            startActivity(mainIntent)
            finish()
        }
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
        val intent = Intent(this, PlayerActivity::class.java)
        putExtras(intent, track)
        viewModel.addTrackToHistory(track)
        historyAdapter.notifyDataSetChanged()
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