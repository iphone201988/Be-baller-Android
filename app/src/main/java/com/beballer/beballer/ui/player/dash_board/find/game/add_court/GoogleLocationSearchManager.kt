package com.beballer.beballer.ui.player.dash_board.find.game.add_court

import android.content.Context
import android.text.Editable
import android.util.Log
import android.widget.AutoCompleteTextView
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GoogleLocationSearchManager(
    private val context: Context,
    private val placesClient: PlacesClient
) {
    private val searchScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun setupSearch(
        autoCompleteTextView: AutoCompleteTextView,
        onResultSelected: (String, Coordinate) -> Unit
    ) {
        autoCompleteTextView.threshold = 1
        val adapter = LocationSuggestionAdapter(context, mutableListOf())
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val (label, coordinate) = adapter.getItem(position)!!
            autoCompleteTextView.setText(label, false)
            autoCompleteTextView.dismissDropDown()
            autoCompleteTextView.clearFocus()
            onResultSelected(label, coordinate)
        }

        autoCompleteTextView.addTextChangedListener(object : android.text.TextWatcher {
            private var searchJob: Job? = null
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty() || !autoCompleteTextView.isFocused) return

                searchJob?.cancel()
                searchJob = searchScope.launch {
                    try {
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(query)
                            .build()
                        val response = placesClient.findAutocompletePredictions(request).await()

                        val addresses = response.autocompletePredictions.map { prediction ->
                            prediction.getFullText(null).toString() to Coordinate(0.0, 0.0)
                        }

                        withContext(Dispatchers.Main) {
                            adapter.updateData(addresses)
                            if (autoCompleteTextView.isFocused && addresses.isNotEmpty()) {
                                autoCompleteTextView.showDropDown()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("GoogleSearchManager", "Error: ${e.localizedMessage}", e)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}

data class Coordinate(val latitude: Double, val longitude: Double)
