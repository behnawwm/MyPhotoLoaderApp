package com.example.myphotoloaderapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyPhoto(
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
    ) : Parcelable

    @Parcelize
    data class User(
        var username: String,
        var name: String
    ) : Parcelable {
        val attributionUrl get() = "https://unsplash.com/$username?utm_source=MyImageLoader&utm_medium=referral"
    }
}