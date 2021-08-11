<h1 align="center">MyPhotoLoaderApp</h1>

Simple photo loading app powered by [Unsplash.com](https://unsplash.com) 

## Features
- Vieweing the high quality image + description of the owner
- Searching among the whole Unsplash photos
- Saving photo to selected path of external storage
- Zooming Image
- Handling high loads of data
- Handling errors and network issues
- viewing saved & favorite photos (todo)

## Tech stack & Open-source libraries
This project is based on MVVM architecture, using following tech-stacks:
- Jetpack
  - Navigation Component
  - Hilt
  - Paging 3
  - Lifecycle
  - View Binding
- Retrofit
- Glide
- Coroutines
- [Zoombale ImageView](https://github.com/stfalcon-studio/StfalconImageViewer)
- [File Picker](https://github.com/spacecowboy/NoNonsense-FilePicker)
- [Downloader](https://github.com/tonyofrancis/Fetch)

## How to use?
Build & install or [Get .APK](https://github.com/behnawwm/MyPhotoLoaderApp/raw/master/Apk/MyPhotoLoader.apk)

If no results were shown:
- Use proxy!
- Replace ``UNSPLASH_ACCESS_KEY`` in ``build.gradle`` with your own access_key from [Unsplash Developers](https://unsplash.com/developers).

## Screenshots:
<p float="left">
  <img src="https://github.com/behnawwm/MyPhotoLoaderApp/blob/master/screenshots/photo5803347900867130570.jpg" width="20%" >
  <img src="https://github.com/behnawwm/MyPhotoLoaderApp/blob/master/screenshots/photo5803347900867130568.jpg" width="20%" >
  <img src="https://github.com/behnawwm/MyPhotoLoaderApp/blob/master/screenshots/photo5803347900867130569.jpg" width="20%" >
  <img src="https://github.com/behnawwm/MyPhotoLoaderApp/blob/master/screenshots/photo5803347900867130566.jpg" width="20%" >
</p>


## Architecture
This app is based on MVVM architecture and a repository pattern.
<p align="center">
<img src="https://developer.android.com/topic/libraries/architecture/images/paging3-library-architecture.svg"/>
</p>
