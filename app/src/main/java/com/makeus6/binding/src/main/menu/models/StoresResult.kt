package com.makeus6.binding.src.main.menu.models

import com.makeus6.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class StoresResult (
    @SerializedName("bookstoreIdx") val bookstoreIdx: Int,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("location") val location: String,
    @SerializedName("storeImgUrl") val storeImgUrl: String
): BaseResponse()