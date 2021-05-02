package com.example.myphotoloaderapp.UI.gallery

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.myphotoloaderapp.data.PhotoRepository
import javax.inject.Inject


class GalleryViewModel @ViewModelInject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)

    var photos = currentQuery.switchMap { queryString ->
        repository.getSearchResults(DEFAULT_QUERY).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    companion object {
        private const val DEFAULT_QUERY = "cats"
    }
}