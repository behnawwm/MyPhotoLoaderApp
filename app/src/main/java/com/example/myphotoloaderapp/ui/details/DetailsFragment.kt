package com.example.myphotoloaderapp.ui.details

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myphotoloaderapp.R
import com.example.myphotoloaderapp.data.model.MyPhoto
import com.example.myphotoloaderapp.databinding.FragmentDetailsBinding
import com.example.myphotoloaderapp.utils.Common.errorToasty
import com.example.myphotoloaderapp.utils.Common.infoToasty
import com.example.myphotoloaderapp.utils.Common.successToasty
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nononsenseapps.filepicker.FilePickerActivity
import com.skydoves.progressview.ProgressView
import com.stfalcon.imageviewer.StfalconImageViewer
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    lateinit var binding: FragmentDetailsBinding
    private val args by navArgs<DetailsFragmentArgs>()
    val viewModel by viewModels<DetailsViewModel>()

    lateinit var contentResolver: ContentResolver
    lateinit var fetch: Fetch

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailsBinding.bind(view)

        contentResolver = activity?.contentResolver!!
        initializeDownloader()

        binding.apply {
            val photo = args.photo

            fillImageView(photo)

            imageView.setOnClickListener {
                val images = listOf(photo.urls.full)

                StfalconImageViewer.Builder(context, images) { view, image ->

                    Glide.with(this@DetailsFragment)
                        .load(image)
                        .error(R.drawable.ic_image_error)
                        .into(view)
                        .onLoadStarted(resources.getDrawable(R.drawable.ic_download))
                }.show()
            }

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

    private fun fillImageView(photo: MyPhoto) {
        binding.apply {
            Glide.with(this@DetailsFragment)
                .load(photo.urls.regular)
                .error(R.drawable.ic_image_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.isVisible = false
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
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        grantStoragePermission()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_details, menu)

        val saveItem = menu.findItem(R.id.menu_details_save)
        saveItem.setOnMenuItemClickListener {

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

    private fun initializeDownloader() {
        val fetchConfiguration: FetchConfiguration =
            FetchConfiguration.Builder(requireContext())
                .setDownloadConcurrentLimit(3)
                .build()

        fetch = Fetch.Impl.getInstance(fetchConfiguration)
    }

    private fun grantStoragePermission() {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
//                    context.successToasty("Permission granted!")
                } else {
                    context.errorToasty("Permission declined! App won't be able to save external files!")
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale("mamad")
            -> {
//                Toasty.warning(requireContext(), "grant permission mamad jan", Toasty.LENGTH_SHORT)
//                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            9999 -> {
                if (data == null)
                    return

                initializeDownloader()
                val download = makeDownloadRequest(data)

                if (download != null) {
                    val mBottomSheetDialog = makeDownloadDialog(download)
                    mBottomSheetDialog.show()

                    enqueueDownloadRequest(fetch, download, mBottomSheetDialog)
                } else {
                    context.infoToasty("Image already downloaded in this path!")
                }
            }
        }
    }

    private fun makeDownloadRequest(pathData: Intent): Request? {
        val url = args.photo.urls.full
        val path = pathData.data?.path?.substringAfter("/root") + "/${args.photo.id}.jpg"

        if (File(path).exists())
            return null


        var download = Request(url, path)
        download.priority = Priority.HIGH
        download.networkType = NetworkType.ALL

        return download
    }


    private fun makeDownloadDialog(download: Request?): BottomSheetDialog {
//        val bottomSheetLoadingBinding =  //todo : changes not appliable
//            BottomSheetLoadingBinding.inflate(LayoutInflater.from(context))

        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        dialog.apply {
            setContentView(R.layout.bottom_sheet_loading)
            setCancelable(false)
            findViewById<Button>(R.id.btn_cancel_bottom_sheet)?.setOnClickListener {
                dialog.dismiss()
                if (download != null) {
                    fetch.cancel(download.id)
                    if (File(download.file).exists())
                        File(download.file).delete()
                }
            }
        }
        return dialog
    }

    private fun enqueueDownloadRequest(
        fetch: Fetch,
        request: Request,
        dialog: BottomSheetDialog
    ) {
        var fetchListener = provideFetchListener(dialog)

        fetch.enqueue(request, {})
        { error ->
            context.errorToasty("error: ${error}")
        }
        fetch.addListener(fetchListener)
    }

    private fun provideFetchListener(dialog: BottomSheetDialog): FetchListener {
        return object : FetchListener {
            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {}
            override fun onCompleted(download: Download) {
                context.successToasty("Download Complete!")
                dialog.dismiss()
            }

            override fun onProgress(
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                Log.d("mamad", "Progress: $download.progress")
                dialog.findViewById<ProgressView>(R.id.pb_loading_bottom_sheet)?.progress =
                    download.progress.toFloat()
            }

            override fun onPaused(download: Download) {}
            override fun onResumed(download: Download) {}
            override fun onStarted(
                download: Download,
                downloadBlocks: List<DownloadBlock>,
                totalBlocks: Int
            ) {
            }

            override fun onWaitingNetwork(download: Download) {}
            override fun onAdded(download: Download) {}
            override fun onCancelled(download: Download) {}
            override fun onRemoved(download: Download) {}
            override fun onDeleted(download: Download) {}
            override fun onDownloadBlockUpdated(
                download: Download,
                downloadBlock: DownloadBlock,
                totalBlocks: Int
            ) {
            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                context.errorToasty("Error Downloading Image: ${error.name}")
                dialog.dismiss()
            }
        }
    }
}

