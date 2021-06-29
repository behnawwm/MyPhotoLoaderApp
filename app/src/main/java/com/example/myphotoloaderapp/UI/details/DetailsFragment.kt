package com.example.myphotoloaderapp.UI.details

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
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
import com.example.myphotoloaderapp.data.MyPhoto
import com.example.myphotoloaderapp.databinding.BottomSheetLoadingBinding
import com.example.myphotoloaderapp.databinding.FragmentDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nononsenseapps.filepicker.FilePickerActivity
import com.skydoves.progressview.ProgressView
import com.stfalcon.imageviewer.StfalconImageViewer
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import es.dmoral.toasty.Toasty


class DetailsFragment : Fragment(R.layout.fragment_details) {

    lateinit var binding: FragmentDetailsBinding
    private val args by navArgs<DetailsFragmentArgs>()
    lateinit var contentResolver: ContentResolver
    lateinit var fetch: Fetch
    val viewmodel by viewModels<DetailsViewModel>()

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
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Toasty.success(requireContext(), "granted", Toasty.LENGTH_SHORT)
                        .show()
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Toasty.error(requireContext(), "declined. why azizam?", Toasty.LENGTH_SHORT)
                        .show()
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
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                Toasty.warning(requireContext(), "grant permission mamad jan", Toasty.LENGTH_SHORT)
                    .show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
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

                var download: Request? = null
                val mBottomSheetDialog = makeDownloadDialog(download)
                mBottomSheetDialog.show()

                initializeDownloader()
                download = makeDownloadRequest(data)
                enqueueDownloadRequest(fetch, download, mBottomSheetDialog)
            }
        }
    }

    private fun makeDownloadRequest(pathData: Intent): Request {
        val url = args.photo.urls.full
        val path = pathData.data?.path?.substringAfter("/root") + "/${args.photo.id}.jpg"

        var download = Request(url, path)
        download.priority = Priority.HIGH
        download.networkType = NetworkType.ALL

        return download
    }


    private fun makeDownloadDialog(download: Request?): BottomSheetDialog {
        val bottomSheetLoadingBinding =
            BottomSheetLoadingBinding.inflate(LayoutInflater.from(context))

        val dialog = BottomSheetDialog(requireContext())
        dialog.apply {
            setContentView(R.layout.bottom_sheet_loading)
            setCancelable(false)
        }
        bottomSheetLoadingBinding.apply {
            btnCancelBottomSheet.setOnClickListener {
                dialog.dismiss()
                if (download != null)
                    fetch.cancel(download.id)
            }
        }
        return dialog

//        return LoadingBottomSheet().show(requireContext()) {
//            style(SheetStyle.BOTTOM_SHEET)
//            title("Custom Example")
//        }

//        return LovelyProgressDialog(requireContext())
//            .setIcon(R.drawable.ic_close)
//            .setTitle("Downloading...")
//            .setTopColorRes(R.color.colorPrimary)
//            .show();

//        return BottomSheetMaterialDialog.Builder(requireActivity())
//            .setTitle("Downloading...")
//            .setCancelable(false)
//            .setAnimation(R.raw.download2)
//            .setNegativeButton(
//                "Cancel", R.drawable.ic_close
//            ) { dialogInterface, which ->
//                dialogInterface.dismiss()
//                if (download != null)
//                    fetch.cancel(download.id)
//            }
//            .build()
    }

    private fun enqueueDownloadRequest(
        fetch: Fetch,
        request: Request,
        dialog: BottomSheetDialog
    ) {
        var fetchListener = provideFetchListener(dialog)

        fetch.enqueue(request, {})
        { error ->
            Toasty.error(
                requireContext(),
                "error: ${error}",
                Toasty.LENGTH_LONG
            ).show()
        }
        fetch.addListener(fetchListener)
    }

    private fun provideFetchListener(dialog: BottomSheetDialog): FetchListener {
        return object : FetchListener {
            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {}
            override fun onCompleted(download: Download) {
                Toasty.success(requireContext(), "Download Complete!", Toasty.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            override fun onProgress(
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                val progress = download.progress
                Log.d("mamad", "Progress: $progress")
                dialog.findViewById<ProgressView>(R.id.pb_loading_bottom_sheet)?.progress =
                    progress.toFloat()
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
                Toasty.error(
                    requireContext(),
                    "Error Downloading Image: ${error.name}",
                    Toasty.LENGTH_LONG
                ).show()
                dialog.dismiss()
            }
        }
    }
}

