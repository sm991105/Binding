package com.makeus6.binding.src.main.home.models

import com.google.gson.annotations.SerializedName

data class PopularResult (
    @SerializedName("viewCount") val viewCount: Int
): NewestResult()