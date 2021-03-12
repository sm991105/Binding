package com.example.binding.src.main.menu.models

import com.example.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetStoresResponse (
    @SerializedName("result") val result: ArrayList<StoresResult>
): BaseResponse()