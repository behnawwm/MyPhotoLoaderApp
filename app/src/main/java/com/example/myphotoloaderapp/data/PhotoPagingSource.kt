package com.example.myphotoloaderapp.data

import androidx.paging.PagingSource
import com.example.myphotoloaderapp.network.PhotoApi
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class PhotoPagingSource(
    var api: PhotoApi,
    var query: String
) : PagingSource<Int, MyPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MyPhoto> {
        val posistion = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = api.searchPhoto(query, posistion, params.loadSize)
            val photos: List<MyPhoto> = response.results

            LoadResult.Page(
                data = photos,
                prevKey = if (posistion == STARTING_PAGE_INDEX) null else posistion - 1,
                nextKey = if (photos.isEmpty()) null else posistion + 1
            )
        } catch (ex: IOException) {
            LoadResult.Error(ex)
        } catch (ex: HttpException) {
            LoadResult.Error(ex)
        }


    }
}