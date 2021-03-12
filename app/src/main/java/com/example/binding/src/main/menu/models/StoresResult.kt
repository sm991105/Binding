package com.example.binding.src.main.menu.models

import com.example.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class StoresResult (
    @SerializedName("bookstoreIdx") val bookstoreIdx: Int,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("location") val location: String,
    @SerializedName("storeImgUrl") val storeImgUrl: String
): BaseResponse()