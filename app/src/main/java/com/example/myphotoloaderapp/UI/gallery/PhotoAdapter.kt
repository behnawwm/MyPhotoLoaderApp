package com.example.myphotoloaderapp.UI.gallery

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myphotoloaderapp.data.MyPhoto
import com.example.myphotoloaderapp.databinding.ItemPhotoBinding

class PhotoAdapter : PagingDataAdapter<MyPhoto, PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }
}