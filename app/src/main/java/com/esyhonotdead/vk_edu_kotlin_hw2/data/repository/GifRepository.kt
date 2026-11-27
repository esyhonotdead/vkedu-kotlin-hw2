package com.esyhonotdead.vk_edu_kotlin_hw2.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.esyhonotdead.vk_edu_kotlin_hw2.data.databases.GifDatabase
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.DEFAULT_RATING
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.GifImage
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.GifImages
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.GifModel
import com.esyhonotdead.vk_edu_kotlin_hw2.data.paging.GifPagingSource
import com.esyhonotdead.vk_edu_kotlin_hw2.data.paging.GifRemoteMediator
import com.esyhonotdead.vk_edu_kotlin_hw2.data.remote.GiphyApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GifRepository {
    fun getGifs(rating: String = DEFAULT_RATING, query: String? = null): Flow<PagingData<GifModel>>
}

class NetworkGifRepository(
    private val api: GiphyApi,
    private val apiKey: String,
    private val database: GifDatabase
) : GifRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getGifs(rating: String, query: String?): Flow<PagingData<GifModel>> {
        val config = PagingConfig(
            pageSize = 25,
            initialLoadSize = 30,
            prefetchDistance = 5,
            enablePlaceholders = true,
        )

        if (query == null && rating == DEFAULT_RATING) {
            return Pager(
                config = config,
                remoteMediator = GifRemoteMediator(api = api, apiKey = apiKey, database = database),
                pagingSourceFactory = { database.gifDao().pagingSource() },
            ).flow.map { page ->
                page.map { gif ->
                    GifModel(
                        id = gif.id,
                        title = gif.title,
                        url = gif.pageUrl.orEmpty(),
                        images = GifImages(
                            fixedHeight = GifImage(
                                url = gif.imageUrl,
                                width = gif.width.toString(),
                                height = gif.height.toString(),
                            )
                        ),
                    )
                }
            }
        }

        return Pager(
            config = config,
            pagingSourceFactory = {
                GifPagingSource(
                    api = api,
                    apiKey = apiKey,
                    rating = rating,
                    query = query
                )
            }
        ).flow
    }
}
