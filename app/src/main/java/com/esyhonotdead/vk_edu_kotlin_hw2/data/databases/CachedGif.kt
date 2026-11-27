package com.esyhonotdead.vk_edu_kotlin_hw2.data.databases

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Upsert
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter

@Entity(tableName = "cached_gifs")
data class CachedGif(
    @PrimaryKey
    val id: String,
    val position: Int,
    val title: String,
    val imageUrl: String,
    val width: Int,
    val height: Int,
    val pageUrl: String? = null,
)

@Entity(tableName = "trending_page_state")
data class TrendingPageState(
    @PrimaryKey
    val id: Int = 0,
    val nextOffset: Int?
)

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
interface GifDao {
    @Query("SELECT * FROM cached_gifs ORDER BY position")
    fun pagingSource(): PagingSource<Int, CachedGif>

    @Upsert
    suspend fun upsertAll(gifs: List<CachedGif>)

    @Query("DELETE FROM cached_gifs WHERE id = :id")
    suspend fun clear(id: String)

    @Query("DELETE FROM cached_gifs")
    suspend fun clearAll()

    @Query("SELECT nextOffset FROM trending_page_state WHERE id = 0")
    suspend fun getNextOffset(): Int?

    @Upsert
    suspend fun savePageState(state: TrendingPageState)
}
