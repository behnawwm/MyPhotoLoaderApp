package com.example.myphotoloaderapp.data

import com.example.myphotoloaderapp.network.PhotoApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(var api: PhotoApi) {


}