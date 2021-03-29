package com.medium.binding.src.main.menu.store_detail.models

import com.medium.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetBookStoreResponse (
    @SerializedName("result") val result: BookStoreResult
): BaseResponse()