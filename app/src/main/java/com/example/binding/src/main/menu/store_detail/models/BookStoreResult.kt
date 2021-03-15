package com.example.binding.src.main.menu.store_detail.models

import com.example.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class BookStoreResult (
    @SerializedName("images") val images: ArrayList<BookStoreImages>,
    @SerializedName("bookStoreInfo") val bookStoreInfo: BookStoreInfo
)