package com.example.myphotoloaderapp.UI.gallery

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.myphotoloaderapp.data.PhotoRepository
import javax.inject.Inject


class GalleryViewModel @ViewModelInject constructor(
    var repository: PhotoRepository
) : ViewModel() {
}