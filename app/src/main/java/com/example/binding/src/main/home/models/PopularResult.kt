package com.example.binding.src.main.home.models

import com.google.gson.annotations.SerializedName

data class PopularResult (
    @SerializedName("viewCount") val images: Int
): NewestResult()