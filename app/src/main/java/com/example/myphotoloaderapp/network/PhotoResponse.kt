package com.example.myphotoloaderapp.network

import com.example.myphotoloaderapp.data.MyPhoto

data class PhotoResponse(
    var results: List<MyPhoto>
) {
}