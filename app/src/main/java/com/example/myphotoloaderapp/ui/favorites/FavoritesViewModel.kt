package com.example.myphotoloaderapp.ui.favorites

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.myphotoloaderapp.data.model.FavoritePhoto
import com.example.myphotoloaderapp.data.repo.FavoriteRepository

class FavoritesViewModel @ViewModelInject constructor(
    private val repo: FavoriteRepository,
) : ViewModel() {

    suspend fun insertFav(photo: FavoritePhoto) {
        repo.insertFav(photo)
    }

    suspend fun updateFav(photo: FavoritePhoto) =
        repo.updateFav(photo)

    suspend fun deleteFav(photo: FavoritePhoto) =
        repo.deleteFav(photo)

    suspend fun deleteAllFav() =
        repo.deleteAllFav()
}