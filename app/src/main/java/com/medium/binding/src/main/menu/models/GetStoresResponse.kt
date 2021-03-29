package com.medium.binding.src.main.menu.models

import com.medium.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetStoresResponse (
    @SerializedName("result") val result: ArrayList<StoresResult>
): BaseResponse()