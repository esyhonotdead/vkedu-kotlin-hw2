package com.esyhonotdead.vk_edu_kotlin_hw2.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.GifModel
import com.esyhonotdead.vk_edu_kotlin_hw2.data.remote.GiphyApi
import kotlin.coroutines.cancellation.CancellationException


//  Это взято из документации GIPHY:
//  query == null → лента популярных GIF, максимальный offset — 499. GIPHY Trending
//  Есть поисковый запрос → максимальный offset — 4999. GIPHY Search
internal const val MAX_TRENDING_OFFSET = 499
private const val MAX_SEARCH_OFFSET = 4999

class GifPagingSource(
    private val api: GiphyApi,
    private val apiKey: String,
    private val rating: String,
    private val query: String? = null
) : PagingSource<Int, GifModel>() {
    private val maxOffset = if (query == null) MAX_TRENDING_OFFSET else MAX_SEARCH_OFFSET

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, GifModel> {
        val offset = params.key ?: 0

        return try {
            val response = if (query == null) {
                api.getTrendingGifs(
                    apiKey = apiKey, limit = params.loadSize, offset = offset, rating = rating
                )
            } else {
                api.searchGifs(
                    apiKey = apiKey,
                    query = query,
                    limit = params.loadSize,
                    offset = offset,
                    rating = rating
                )
            }

            val nextOffset = offset + response.data.size
            val totalCount = response.pagination.totalCount

            LoadResult.Page(
                data = response.data, prevKey = null, nextKey = when {
                    response.data.isEmpty() -> null
                    nextOffset > maxOffset -> null
                    totalCount != null && nextOffset >= totalCount -> null
                    else -> nextOffset
                }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, GifModel>): Int? = null
}
