package com.medium.binding.src.main.home.models

import com.google.gson.annotations.SerializedName

data class ReportBody(@SerializedName("reportReason") val reportReason: String)