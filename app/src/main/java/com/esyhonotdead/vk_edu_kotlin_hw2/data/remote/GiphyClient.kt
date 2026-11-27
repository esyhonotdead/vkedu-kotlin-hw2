package com.esyhonotdead.vk_edu_kotlin_hw2.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GiphyClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.giphy.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: GiphyApi = retrofit.create(GiphyApi::class.java)
}
