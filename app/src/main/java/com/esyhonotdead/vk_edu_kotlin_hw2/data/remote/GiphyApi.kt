package com.esyhonotdead.vk_edu_kotlin_hw2.data.remote

import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.DEFAULT_RATING
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.GifsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyApi {
    @GET("v1/gifs/search")
    suspend fun searchGifs(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("rating") rating: String
    ): GifsResponse

    @GET("v1/gifs/trending")
    suspend fun getTrendingGifs(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0,
        @Query("rating") rating: String = DEFAULT_RATING
    ): GifsResponse
}
