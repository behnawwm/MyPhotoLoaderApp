package com.example.myphotoloaderapp.UI.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.myphotoloaderapp.R
import com.example.myphotoloaderapp.data.MyPhoto
import com.example.myphotoloaderapp.databinding.ItemPhotoBinding

class PhotoAdapter(val listener: OnItemPressListener) :
    PagingDataAdapter<MyPhoto, PhotoAdapter.PhotoViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null)
            holder.bind(currentItem)
    }

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                var position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    var item = getItem(position)
                    if (item != null) {
                        listener.OnItemClick(item)
                    }
                }
            }
        }

        fun bind(photo: MyPhoto) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.urls.regular)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_image_error)
                    .into(binding.imageView)

                textViewUserName.text = photo.user.username
            }
        }

    }

    interface OnItemPressListener {
        fun OnItemClick(photo: MyPhoto)
    }

    object DIFF_CALLBACK : DiffUtil.ItemCallback<MyPhoto>() {
        override fun areItemsTheSame(oldItem: MyPhoto, newItem: MyPhoto): Boolean =
            newItem.id == oldItem.id


        override fun areContentsTheSame(oldItem: MyPhoto, newItem: MyPhoto): Boolean =
            newItem == oldItem

    }
}