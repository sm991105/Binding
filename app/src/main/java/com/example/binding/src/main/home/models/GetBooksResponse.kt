package com.example.binding.src.main.home.models

import com.example.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetBooksResponse (
    @SerializedName("viewCount") val viewCount: String,
    @SerializedName("bookIdx") val bookIdx: String,
    @SerializedName("bookImgUrl") val bookImgUrl: String,
    @SerializedName("bookName") val bookName: String,
    @SerializedName("authorName") val authorName: String,
    @SerializedName("contentsCount") val contentsCount: String
): BaseResponse()