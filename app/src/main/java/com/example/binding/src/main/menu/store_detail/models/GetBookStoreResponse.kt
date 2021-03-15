package com.example.binding.src.main.menu.store_detail.models

import com.example.binding.config.BaseResponse
import com.example.binding.src.main.menu.models.StoresResult
import com.google.gson.annotations.SerializedName

data class GetBookStoreResponse (
    @SerializedName("result") val result: BookStoreResult
): BaseResponse()