package com.medium.binding.src.main.home.models

import com.google.gson.annotations.SerializedName

data class CommentsBody(@SerializedName("contents") val contents: String)