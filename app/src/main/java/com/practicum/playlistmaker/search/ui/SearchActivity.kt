package com.practicum.playlistmaker.search.ui

import Creator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val SEARCH_REFERENCES = "search_activity_preferences"
const val SEARCH_KEY = "key_for_search"

class SearchActivity : AppCompatActivity(), TracksAdapter.TrackListener {

    @SuppressLint("MissingInflatedId", "ResourceAsColor", "WrongViewCast")

    private lateinit var editText: EditText

    private var tracks: ArrayList<TrackDto> = ArrayList()

    private val retrofit = RetrofitNetworkClient()
    private val itunesService = retrofit.getService()

    private val adapter = TracksAdapter(tracks, this)

    private val searchHistoryList = mutableListOf<TrackDto>()

    private var historyMap = mutableMapOf<String, String>()
    private val historyAdapter = TracksAdapter(searchHistoryList, this)

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPrefs = getSharedPreferences(SEARCH_REFERENCES, MODE_PRIVATE)
        val searchHistoryLayout = findViewById<RelativeLayout>(R.id.search_history)

        val recycler = findViewById<RecyclerView>(R.id.tracksList)
        recycler.layoutManager = LinearLayoutManager(this)

        val historyRecycler = findViewById<RecyclerView>(R.id.search_history_list)
        historyRecycler.layoutManager = LinearLayoutManager(this)

        val connectionImage = findViewById<ImageView>(R.id.connection_image)
        val connectionMessage = findViewById<TextView>(R.id.connection_text)
        val connectionExtraMessage = findViewById<TextView>(R.id.extra_connection_text)
        val updateButton = findViewById<Button>(R.id.update_search_button)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)

        val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)
        val searchHistoryListRecycler = RecyclerView(this)

        val returnButton = findViewById<MaterialButton>(R.id.search_return_button)
        returnButton.setOnClickListener {
            finish()
        }

        val searchView = findViewById<SearchView>(R.id.search_bar)
        editText =
            (searchView.findViewById(androidx.appcompat.R.id.search_src_text))

        if (sharedPrefs.getString(SEARCH_KEY, "").toString().isNotEmpty()) {
            val historyListOfTracks = searchHistoryInteractor.getHistory(searchHistoryListRecycler)
            addSearchHistory(historyListOfTracks, historyRecycler)
            (editText).setOnFocusChangeListener { _, hasFocus ->
                searchHistoryLayout.visibility =
                    if (editText.text.isEmpty() && searchHistoryList.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }

        val searchRunnable = Runnable {
            itunesServiceSearch(
                connectionImage,
                connectionMessage,
                connectionExtraMessage,
                updateButton,
                recycler,
                editText,
                progressBar
            )
        }

        editText.addTextChangedListener(
            beforeTextChanged = { _: CharSequence?, _, _, _ -> },
            onTextChanged = { s: CharSequence?, _, _, _ ->
                connectionImage.visibility = View.GONE
                connectionMessage.visibility = View.GONE
                connectionExtraMessage.visibility = View.GONE
                updateButton.visibility = View.GONE
                tracks.clear()
                recycler.adapter = adapter

                searchDebounce(searchRunnable)

                searchHistoryLayout.visibility =
                    if (editText.hasFocus() && s?.isEmpty() == true && sharedPrefs.getString(
                            SEARCH_KEY,
                            ""
                        ).toString().isNotEmpty()
                    ) View.VISIBLE else View.GONE
            },
            afterTextChanged = { _ -> }
        )

        val searchCloseButtonId =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn).id
        val closeButton = searchView.findViewById<ImageView>(searchCloseButtonId)
        closeButton.setOnClickListener {
            editText.text.clear()

            connectionImage.visibility = View.GONE
            connectionMessage.visibility = View.GONE
            connectionExtraMessage.visibility = View.GONE
            updateButton.visibility = View.GONE
            tracks.clear()
            recycler.adapter = adapter

            val view: View? = this.currentFocus
            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                itunesServiceSearch(
                    connectionImage,
                    connectionMessage,
                    connectionExtraMessage,
                    updateButton,
                    recycler,
                    editText,
                    progressBar
                )
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        updateButton.setOnClickListener {
            itunesServiceSearch(
                connectionImage,
                connectionMessage,
                connectionExtraMessage,
                updateButton,
                recycler,
                editText,
                progressBar
            )
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistory(historyRecycler, searchHistoryLayout)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run { putString("KEY", editText.text.toString()) }
        super.onSaveInstanceState(outState)
    }

    fun getSearchingTracks(
        connectionImage: ImageView,
        connectionMessage: TextView,
        connectionExtraMessage: TextView,
        updateButton: Button,
        response: Response<TracksSearchResponse>,
        recycler: RecyclerView
    ) {
        tracks.clear()

        connectionImage.visibility = View.GONE
        connectionMessage.visibility = View.GONE
        connectionExtraMessage.visibility = View.GONE
        updateButton.visibility = View.GONE
        tracks.addAll(response.body()?.results!!)
        recycler.adapter = adapter
    }

    fun setEmptySearchParameters(
        connectionImage: ImageView,
        connectionMessage: TextView,
        connectionExtraMessage: TextView,
        updateButton: Button,
        image: Int,
        message: Int
    ) {
        connectionImage.visibility = View.VISIBLE
        connectionMessage.visibility = View.VISIBLE
        connectionExtraMessage.visibility = View.GONE
        updateButton.visibility = View.GONE
        connectionImage.setImageResource(image)
        connectionMessage.setText(message)
    }

    fun setFailureConnectionParameters(
        connectionImage: ImageView,
        connectionMessage: TextView,
        connectionExtraMessage: TextView,
        updateButton: Button,
        image: Int,
        message: Int,
        extraMessage: Int
    ) {
        connectionImage.visibility = View.VISIBLE
        connectionMessage.visibility = View.VISIBLE
        connectionExtraMessage.visibility = View.VISIBLE
        updateButton.visibility = View.VISIBLE
        connectionImage.setImageResource(image)
        connectionMessage.setText(message)
        connectionExtraMessage.setText(extraMessage)
    }

    fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }

    fun itunesServiceSearch(
        connectionImage: ImageView,
        connectionMessage: TextView,
        connectionExtraMessage: TextView,
        updateButton: Button,
        recycler: RecyclerView,
        text: EditText,
        progressBar: ProgressBar
    ) {
        val nameOfSearchingTrack: Editable? = editText.text
        progressBar.visibility = View.VISIBLE

        itunesService
            .search(nameOfSearchingTrack.toString())
            .enqueue(object : Callback<TracksSearchResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TracksSearchResponse>,
                    response: Response<TracksSearchResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            getSearchingTracks(
                                connectionImage,
                                connectionMessage,
                                connectionExtraMessage,
                                updateButton,
                                response,
                                recycler
                            )
                        } else if (text.text.isNotEmpty()) {
                            tracks.clear()
                            recycler.adapter = adapter
                            if (!isDarkModeOn()) {
                                setEmptySearchParameters(
                                    connectionImage,
                                    connectionMessage,
                                    connectionExtraMessage,
                                    updateButton,
                                    R.drawable.empty_search_image,
                                    R.string.empty_search
                                )
                            } else {
                                setEmptySearchParameters(
                                    connectionImage,
                                    connectionMessage,
                                    connectionExtraMessage,
                                    updateButton,
                                    R.drawable.empty_search_image_dark_mode,
                                    R.string.empty_search
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TracksSearchResponse>, t: Throwable) {
                    tracks.clear()
                    progressBar.visibility = View.GONE
                    recycler.adapter = adapter
                    if (!isDarkModeOn()) {
                        setFailureConnectionParameters(
                            connectionImage,
                            connectionMessage,
                            connectionExtraMessage,
                            updateButton,
                            R.drawable.connection_error_image,
                            R.string.connection_error_message,
                            R.string.extra_connection_error_message
                        )
                    } else {
                        setFailureConnectionParameters(
                            connectionImage,
                            connectionMessage,
                            connectionExtraMessage,
                            updateButton,
                            R.drawable.connection_error_image_dark_mode,
                            R.string.connection_error_message,
                            R.string.extra_connection_error_message
                        )
                    }
                }
            })
    }

    private fun searchDebounce(searchRunnable: Runnable) {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onTrackClick(track: TrackDto) {
        val intent = Intent(this, PlayerActivity::class.java)

        putExtras(intent, track)

        startActivity(intent)

        val trackGson = Gson().toJson(track)
        val searchHistoryListRecycler = findViewById<RecyclerView>(R.id.search_history_list)
        val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

        if (historyMap.keys.contains(track.trackId)) {
            historyMap.remove(track.trackId)
            searchHistoryList.remove(track)
        }

        if (historyMap.keys.size == 10) {
            historyMap.remove(historyMap.keys.first())
            searchHistoryList.removeAt(9)
        }

        historyMap[track.trackId] = trackGson
        searchHistoryList.add(0, track)

        searchHistoryInteractor.saveHistory(historyMap)

        searchHistoryListRecycler.layoutManager = LinearLayoutManager(this)
        searchHistoryListRecycler.adapter = historyAdapter
    }

    private fun addSearchHistory(
        historyList: MutableList<TrackDto>,
        searchHistoryListRecycler: RecyclerView
    ) {
        searchHistoryList.addAll(historyList)

        for (track in searchHistoryList) {
            historyMap[track.trackId] = Gson().toJson(track)
        }

        searchHistoryListRecycler.layoutManager = LinearLayoutManager(this)
        searchHistoryListRecycler.adapter = historyAdapter
    }

    private fun clearSearchHistory(
        historyRecycler: RecyclerView,
        searchHistory: RelativeLayout
    ) {
        val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

        searchHistoryList.clear()
        searchHistoryInteractor.clearHistory()

        historyRecycler.adapter = adapter
        searchHistory.visibility = View.GONE
    }

    private fun putExtras(intent: Intent, track: TrackDto) {
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