package com.example.binding.src.main.home.models

import com.example.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetNewestResponse (
    @SerializedName("result") val result: ArrayList<NewestResult>
): BaseResponse()