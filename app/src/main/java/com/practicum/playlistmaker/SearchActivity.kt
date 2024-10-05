package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "ResourceAsColor", "WrongViewCast")

    private var editText: TextView? = null
    private var tracks: ArrayList<Track> = ArrayList()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recycler = findViewById<RecyclerView>(R.id.tracksList)
        recycler.layoutManager = LinearLayoutManager(this)

        val connectionImage = findViewById<ImageView>(R.id.connection_image)
        val connectionMessage = findViewById<TextView>(R.id.connection_text)
        val connectionExtraMessage = findViewById<TextView>(R.id.extra_connection_text)
        val updateButton = findViewById<Button>(R.id.update_search_button)

        val returnButton = findViewById<MaterialButton>(R.id.search_return_button)
        returnButton.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        val searchView = findViewById<SearchView>(R.id.search_bar)
        editText =
            (searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text))

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        (editText as EditText).addTextChangedListener(textWatcher)

        val searchCloseButtonId =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn).id
        val closeButton = searchView.findViewById<ImageView>(searchCloseButtonId)
        closeButton.setOnClickListener {
            (editText as EditText).text.clear()

            connectionImage.visibility = View.GONE
            connectionMessage.visibility = View.GONE
            connectionExtraMessage.visibility = View.GONE
            updateButton.visibility = View.GONE

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
                    recycler
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
                recycler
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run { putString("KEY", editText?.text.toString()) }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editText?.text = savedInstanceState.getString("KEY")
    }

    fun getSearchingTracks(
        connectionImage: ImageView,
        connectionMessage: TextView,
        connectionExtraMessage: TextView,
        updateButton: Button,
        response: Response<TracksResponse>,
        recycler: RecyclerView
    ) {
        tracks.clear()
        connectionImage.visibility = View.GONE
        connectionMessage.visibility = View.GONE
        connectionExtraMessage.visibility = View.GONE
        updateButton.visibility = View.GONE
        tracks.addAll(response.body()?.results!!)
        recycler.adapter = TracksAdapter(tracks)
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
        recycler: RecyclerView
    ) {
        val nameOfSearchingTrack: Editable? = (editText as EditText).text
        itunesService
            .search(nameOfSearchingTrack.toString())
            .enqueue(object : Callback<TracksResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
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
                        } else {
                            tracks.clear()
                            recycler.adapter = TracksAdapter(tracks)
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

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    tracks.clear()
                    recycler.adapter = TracksAdapter(tracks)
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
}