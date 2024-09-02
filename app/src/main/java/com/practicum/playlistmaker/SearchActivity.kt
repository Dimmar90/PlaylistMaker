package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "ResourceAsColor", "WrongViewCast")

    var editText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val returnButton = findViewById<MaterialButton>(R.id.search_return_button)
        returnButton.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        val searchView = findViewById<SearchView>(R.id.search_bar)

        editText =
            (searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text) as EditText)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Toast.makeText(this@SearchActivity, "Поиск: " + s, Toast.LENGTH_SHORT)
                    .show()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        (editText as EditText).addTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run { putString("KEY", editText?.text.toString()) }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editText?.text = savedInstanceState.getString("KEY")
    }
}