package com.medium.binding.src.main.my_page.models

import com.google.gson.annotations.SerializedName

data class UserResult (
    // 사용자 정보
    @SerializedName("info") val info: ArrayList<InfoData>,
    // 사용자가 쓴 글 리스트
    @SerializedName("writing") val writing: ArrayList<ArrayList<WritingData>>,
    // 사용자가 북마크한 글 리스트
    @SerializedName("writingBookmark") val writingBookMark: ArrayList<ArrayList<WBookMarkData>>,
    // 사용자가 북마크한 서점 리스트
    @SerializedName("bookstoreBookmark") val bookstoreBookMark: ArrayList<ArrayList<SBookMarkData>>
)