package com.makeus6.binding.src.main.menu.models

import com.makeus6.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetStoresResponse (
    @SerializedName("result") val result: ArrayList<StoresResult>
): BaseResponse()