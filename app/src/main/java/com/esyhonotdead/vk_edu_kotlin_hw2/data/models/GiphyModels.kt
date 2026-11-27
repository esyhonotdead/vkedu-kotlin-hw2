package com.esyhonotdead.vk_edu_kotlin_hw2.data.models

import com.google.gson.annotations.SerializedName

internal const val RATING_G = "g"
internal const val RATING_PG = "pg"
internal const val RATING_PG_13 = "pg-13"
internal const val RATING_R = "r"
internal const val DEFAULT_RATING = RATING_G

data class GifsResponse(
    val data: List<GifModel>, val pagination: Pagination
)

data class Pagination(
    @SerializedName("total_count") val totalCount: Int? = null, val count: Int, val offset: Int
)

data class GifModel(
    val id: String, val title: String, val url: String, val images: GifImages
)

data class GifImages(
    @SerializedName("fixed_height") val fixedHeight: GifImage
)

data class GifImage(
    val url: String, val width: String, val height: String
)
