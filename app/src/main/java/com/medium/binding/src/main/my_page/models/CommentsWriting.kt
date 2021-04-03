package com.medium.binding.src.main.my_page.models

import com.google.gson.annotations.SerializedName

data class CommentsWriting (
    @SerializedName("userImgUrl") val userImgUrl: String, // 유저 프로필 사진
    @SerializedName("userIdx") val userIdx: Int,        // 유저 인덱스
    @SerializedName("nickname") val nickname: String,   // 유저 닉네임
    @SerializedName("createdAt") val createdAt: String, // 만들어진 날짜
    @SerializedName("isBookMark") var isBookMark: Int, // 1- 북마크, 0 - 북마크 x
    @SerializedName("contentsIdx") val contentsIdx: Int,    // 글 인덱스
    @SerializedName("contents") val contents: String       // 글 내용
)