package com.example.myphotoloaderapp.UI.details

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myphotoloaderapp.R
import com.example.myphotoloaderapp.databinding.FragmentDetailsBinding
import com.nononsenseapps.filepicker.FilePickerActivity


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailsBinding.bind(view)

        binding.apply {
            val photo = args.photo

            Glide.with(this@DetailsFragment)
                .load(photo.urls.full)
                .error(R.drawable.ic_image_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        textViewCreator.isVisible = true
                        textViewDescription.isVisible = photo.desc != null
                        return false
                    }
                })
                .into(imageView)

            textViewDescription.text = photo.desc

            val uri = Uri.parse(photo.user.attributionUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            textViewCreator.apply {
                text = "Photo by ${photo.user.name} on Unsplash"
                setOnClickListener {
                    context.startActivity(intent)
                }
                paint.isUnderlineText = true
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_details, menu)

        val saveItem = menu.findItem(R.id.menu_details_save)
        saveItem.setOnMenuItemClickListener {
//            val config = PRDownloaderConfig.newBuilder()
//                .setReadTimeout(30000)
//                .setConnectTimeout(30000)
//                .build()
//            PRDownloader.initialize(requireContext(), config)
//
//            PRDownloader.download(args.photo.urls.regular)

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//                i.addCategory(Intent.CATEGORY_DEFAULT)
//                startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999)
//            }


            // This always works
//            val i = Intent(context, FilePickerActivity::class.java)
            // This works if you defined the intent filter
            val i = Intent(Intent.ACTION_GET_CONTENT);

            // Set these depending on your use case. These are the defaults.
            // This works if you defined the intent filter
            // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

            // Set these depending on your use case. These are the defaults.
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false)
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR)

            // Configure initial directory by specifying a String.
            // You could specify a String like "/storage/emulated/0/", but that can
            // dangerous. Always use Android's API calls to get paths to the SD-card or
            // internal memory.

            // Configure initial directory by specifying a String.
            // You could specify a String like "/storage/emulated/0/", but that can
            // dangerous. Always use Android's API calls to get paths to the SD-card or
            // internal memory.
            i.putExtra(
                FilePickerActivity.EXTRA_START_PATH,
                Environment.getExternalStorageDirectory().path
            )

            startActivityForResult(i, 9999)


//            MaterialFilePicker()
//                // Pass a source of context. Can be:
//                //    .withActivity(Activity activity)
//                //    .withFragment(Fragment fragment)
//                //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
//                .withActivity(activity)
//                // With cross icon on the right side of toolbar for closing picker straight away
//                .withCloseMenu(true)
//                // Showing hidden files
//                .withHiddenFiles(true)
//                // Want to choose only jpg images
////                .withFilter(Pattern.compile(".*\\.(jpg|jpeg)$"))
//                // Don't apply filter to directories names
//                .withFilterDirectories(false)
//                .withTitle("Sample title")
//                .withRequestCode(9999)
//                .start()

            true
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            9999 ->
                Toast.makeText(context, data.toString(), Toast.LENGTH_LONG).show()
        }
    }
}