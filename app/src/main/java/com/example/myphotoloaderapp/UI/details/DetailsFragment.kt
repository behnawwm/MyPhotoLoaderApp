package com.example.myphotoloaderapp.UI.details

import android.Manifest
import android.content.ContentResolver
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
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.example.myphotoloaderapp.R
import com.example.myphotoloaderapp.databinding.FragmentDetailsBinding
import com.nononsenseapps.filepicker.FilePickerActivity
import com.permissionx.guolindev.PermissionX
import com.tonyodev.fetch2.*
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import es.dmoral.toasty.Toasty


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()
    lateinit var contentResolver: ContentResolver
    lateinit var fetch: Fetch


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailsBinding.bind(view)

        contentResolver = activity?.contentResolver!!
        initializeDownloader()

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

    private fun initializeDownloader() {
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(requireContext(), config)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_details, menu)

        val saveItem = menu.findItem(R.id.menu_details_save)
        saveItem.setOnMenuItemClickListener {
            grantStoragePermission()

            val chooseFolderIntent = Intent(Intent.ACTION_GET_CONTENT);
            chooseFolderIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
            chooseFolderIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true)
            chooseFolderIntent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR)

            chooseFolderIntent.putExtra(
                FilePickerActivity.EXTRA_START_PATH,
                Environment.getExternalStorageDirectory().path
            )

            startActivityForResult(chooseFolderIntent, 9999)

            true
        }

    }

    private fun grantStoragePermission() {
        PermissionX.init(requireActivity())
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(
                        requireContext(),
                        "All permissions are granted",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            9999 -> {
                var download: Request? = null

                val mBottomSheetDialog = BottomSheetMaterialDialog.Builder(requireActivity())
                    .setTitle("Downloading...")
                    .setCancelable(false)
                    .setAnimation(R.raw.download2)
                    .setNegativeButton(
                        "Cancel", R.drawable.ic_close
                    ) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        fetch.cancel(download!!.id)
                    }
                    .build()
                mBottomSheetDialog.show()


                val fetchConfiguration: FetchConfiguration =
                    FetchConfiguration.Builder(requireContext())
                        .setDownloadConcurrentLimit(3)
                        .build()

                fetch = Fetch.Impl.getInstance(fetchConfiguration)


                val url = args.photo.urls.full
                val file = data?.data?.path

                val request = Request(url, file!!)
                request.priority = Priority.HIGH
                request.networkType = NetworkType.ALL

                fetch.enqueue(request, { updatedRequest ->

                })
                { error ->
                    Toasty.error(
                        requireContext(),
                        "error: ${error.toString()}",
                        Toasty.LENGTH_LONG
                    ).show()
                }

            }
        }
    }


}

