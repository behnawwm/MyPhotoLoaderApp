package com.example.myphotoloaderapp.data.local.dao

import androidx.room.*
import com.example.myphotoloaderapp.data.model.FavoritePhoto

@Dao
interface FavoritePhotoDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: FavoritePhoto)

    @Delete
    suspend fun delete(photo: FavoritePhoto)

    @Update
    suspend fun update(photo: FavoritePhoto)

    @Query("DELETE FROM favorite_database")
    suspend fun deleteAll()
}