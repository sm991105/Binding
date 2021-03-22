package com.makeus6.binding.src.main.home.models

import com.google.gson.annotations.SerializedName

data class PopularResult (
    @SerializedName("viewCount") val viewCount: Int,
    @SerializedName("bookIdx") val bookIdx: Int,
    @SerializedName("bookImgUrl") val bookImgUrl: String?,
    @SerializedName("bookName") val bookName: String?,
    @SerializedName("authorName") val authorName: String?,
    @SerializedName("contentsCount") val contentsCount: String?
)