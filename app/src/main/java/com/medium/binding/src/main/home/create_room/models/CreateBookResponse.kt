package com.medium.binding.src.main.home.create_room.models

import com.google.gson.annotations.SerializedName
import com.medium.binding.config.BaseResponse

class CreateBookResponse (
    @SerializedName("bookIdx") val bookIdx: Int
    ): BaseResponse()