package com.makeus6.binding.src.main.menu.store_detail.models

import com.makeus6.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetBookStoreResponse (
    @SerializedName("result") val result: BookStoreResult
): BaseResponse()