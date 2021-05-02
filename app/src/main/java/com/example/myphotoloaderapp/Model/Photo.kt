package com.example.myphotoloaderapp.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    var id: String,
    var desc: String?,
    var urls: PhotoUrls,
    var user: User
) : Parcelable {


    @Parcelize
    data class PhotoUrls(
        var raw: String,
        var full: String,
        var regular: String,
        var thumb: String
    ) : Parcelable {

    }

    @Parcelize
    data class User(
        var username: String,
        var name: String
    ) : Parcelable {
        val attributionUrl get() = "https://unsplash.com/$username?utm_source=MyImageLoader&utm_medium=referral"
    }
}