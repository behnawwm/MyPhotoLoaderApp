package com.example.myphotoloaderapp.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "favorite_database")
@Parcelize
data class FavoritePhoto(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val saveTime: Long,
    val desc: String?,
    @Embedded val urls: PhotoUrls,
    @Embedded val user: User
) : Parcelable {
    @Parcelize
    data class PhotoUrls(
        var raw: String,
        var full: String,
        var regular: String,
        var thumb: String
    ) : Parcelable

    @Parcelize
    data class User(
        var username: String,
        var name: String
    ) : Parcelable {
        val attributionUrl get() = "https://unsplash.com/$username?utm_source=MyImageLoader&utm_medium=referral"
    }
}
