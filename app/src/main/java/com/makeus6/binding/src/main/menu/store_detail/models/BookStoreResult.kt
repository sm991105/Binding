package com.makeus6.binding.src.main.menu.store_detail.models

import com.google.gson.annotations.SerializedName

data class BookStoreResult (
    @SerializedName("images") val images: ArrayList<BookStoreImages>,
    @SerializedName("bookStoreInfo") val bookStoreInfo: ArrayList<BookStoreInfo>
)