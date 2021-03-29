package com.medium.binding.src.main.home.models

import com.medium.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetPopularResponse (
    @SerializedName("result") val result: ArrayList<PopularResult>
): BaseResponse()