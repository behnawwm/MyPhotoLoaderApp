package com.example.myphotoloaderapp.data.repo

import com.example.myphotoloaderapp.data.local.dao.FavoritePhotoDao
import com.example.myphotoloaderapp.data.model.FavoritePhoto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    val dao: FavoritePhotoDao
) {

    suspend fun insertFav(photo: FavoritePhoto) =
        dao.insert(photo)

    suspend fun updateFav(photo: FavoritePhoto) =
        dao.update(photo)

    suspend fun deleteFav(photo: FavoritePhoto) =
        dao.delete(photo)

    suspend fun deleteAllFav() =
        dao.deleteAll()
}