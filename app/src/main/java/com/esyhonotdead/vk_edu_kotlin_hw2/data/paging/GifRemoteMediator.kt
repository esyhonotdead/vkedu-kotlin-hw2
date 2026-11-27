package com.esyhonotdead.vk_edu_kotlin_hw2.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room3.withWriteTransaction
import com.esyhonotdead.vk_edu_kotlin_hw2.data.databases.CachedGif
import com.esyhonotdead.vk_edu_kotlin_hw2.data.databases.GifDatabase
import com.esyhonotdead.vk_edu_kotlin_hw2.data.databases.TrendingPageState
import com.esyhonotdead.vk_edu_kotlin_hw2.data.remote.GiphyApi
import java.io.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class GifRemoteMediator(
    private val api: GiphyApi,
    private val apiKey: String,
    private val database: GifDatabase,
) : RemoteMediator<Int, CachedGif>() {
    private val gifDao = database.gifDao()

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, CachedGif>
    ): MediatorResult {
        return try {
            val offset = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> gifDao.getNextOffset() ?: return MediatorResult.Success(
                    endOfPaginationReached = true
                )
            }

            val limit = if (loadType == LoadType.REFRESH) {
                state.config.initialLoadSize
            } else {
                state.config.pageSize
            }
            val response = api.getTrendingGifs(apiKey = apiKey, limit = limit, offset = offset)
            val nextOffset = response.pagination.offset + response.data.size
            val totalCount = response.pagination.totalCount
            val endReached =
                response.data.isEmpty() || nextOffset > MAX_TRENDING_OFFSET || (totalCount != null && nextOffset >= totalCount)

            val cachedGifs = response.data.mapIndexed { index, gifModel ->
                val image = gifModel.images.fixedHeight
                CachedGif(
                    id = gifModel.id,
                    position = response.pagination.offset + index,
                    title = gifModel.title,
                    imageUrl = image.url,
                    width = image.width.toIntOrNull() ?: 0,
                    height = image.height.toIntOrNull() ?: 0,
                    pageUrl = gifModel.url,
                )
            }

            database.withWriteTransaction {
                if (loadType == LoadType.REFRESH) {
                    gifDao.clearAll()
                }
                gifDao.upsertAll(cachedGifs)
                gifDao.savePageState(TrendingPageState(nextOffset = nextOffset.takeUnless { endReached }))
            }

            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
