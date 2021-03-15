package com.example.binding.src.main.menu.store_detail.models

import com.example.binding.config.BaseResponse
import com.example.binding.src.main.menu.models.StoresResult
import com.google.gson.annotations.SerializedName

data class BookStoreInfo (
    @SerializedName("storeName") val storeName: String,     // 서점 이름
    @SerializedName("isBookMark") val isBookMark: Int,      // 북마크 여부
    @SerializedName("location") val location: String,       // 서점 주소
    @SerializedName("storeTime") val storeTime: String,     // 서점 운영 시간
    @SerializedName("siteAddress") val siteAddress: String, // 서점 웹주소
    @SerializedName("phoneNumber") val phoneNumber: String, // 서점 전화번호
    @SerializedName("storeInfo") val storeInfo: String      // 서점 설명
)