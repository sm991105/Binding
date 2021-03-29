package com.medium.binding.src.main.my_page.models

import com.google.gson.annotations.SerializedName

// 사용자가 북마크한 서점 리스트
data class SBookMarkData(
    @SerializedName("bookstoreIdx") val bookstoreIdx: Int,  // 서점 인덱스
    @SerializedName("storeName") val storeName: String,     // 서점 이름
    @SerializedName("storeImgUrl") val storeImgUrl: String  // 서점 이미지 url
    )
