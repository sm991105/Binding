package com.makeus6.binding.src.main.home.create_room.models

import com.google.gson.annotations.SerializedName

class CreateBookBody (
    @SerializedName("bookName") val bookName: String,       // 책 이름
    @SerializedName("authorName") val authorName: String,   // 저자명
    @SerializedName("bookImgUrl") val bookImgUrl: String    // 책 이미지 url
    )