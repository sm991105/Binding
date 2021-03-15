package com.example.binding.src.main.menu.store_detail.models

import com.example.binding.config.BaseResponse
import com.example.binding.src.main.menu.models.StoresResult
import com.google.gson.annotations.SerializedName

data class BookStoreImages (
    @SerializedName("imageIdx") val imageIdx: Int,      // 이미지 인덱스
    @SerializedName("imageUrl") val imageUrl: String    // 이미지 url

)