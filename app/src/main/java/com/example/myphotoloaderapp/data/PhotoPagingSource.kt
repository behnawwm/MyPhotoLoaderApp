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
        var posistion = params.key ?: STARTING_PAGE_INDEX

        return try {
            var response = api.searchPhoto(query, posistion, params.loadSize)
            var photos: List<MyPhoto> = response.results

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