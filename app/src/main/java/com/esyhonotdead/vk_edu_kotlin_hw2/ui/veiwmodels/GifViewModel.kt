package com.esyhonotdead.vk_edu_kotlin_hw2.ui.veiwmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.cachedIn
import com.esyhonotdead.vk_edu_kotlin_hw2.BuildConfig
import com.esyhonotdead.vk_edu_kotlin_hw2.data.databases.GifDatabase
import com.esyhonotdead.vk_edu_kotlin_hw2.data.remote.GiphyClient
import com.esyhonotdead.vk_edu_kotlin_hw2.data.repository.GifRepository
import com.esyhonotdead.vk_edu_kotlin_hw2.data.repository.NetworkGifRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

data class GifSearch(
    val query: String,
    val rating: String
)

class GifViewModel(
    private val repository: GifRepository
) : ViewModel() {
    private val _activeSearch = MutableStateFlow<GifSearch?>(null)
    val activeSearch = _activeSearch.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val gifs = activeSearch.flatMapLatest { search ->
        if (search == null) {
            repository.getGifs()
        } else {
            repository.getGifs(rating = search.rating, query = search.query)
        }
    }.cachedIn(viewModelScope)

    fun search(query: String, rating: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotEmpty()) {
            _activeSearch.value = GifSearch(trimmedQuery, rating)
        }
    }

    fun clearSearch() {
        _activeSearch.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = GifDatabase.getInstance(application)

                GifViewModel(
                    repository = NetworkGifRepository(
                        GiphyClient.api,
                        apiKey = BuildConfig.GIPHY_API_KEY,
                        database = database
                    )
                )
            }
        }
    }
}
