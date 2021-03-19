package com.makeus6.binding.src.main.home.models

import com.makeus6.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetPopularResponse (
    @SerializedName("result") val result: ArrayList<PopularResult>
): BaseResponse()