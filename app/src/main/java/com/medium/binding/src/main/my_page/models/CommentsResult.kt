package com.medium.binding.src.main.my_page.models

import com.google.gson.annotations.SerializedName

data class CommentsResult (
    // 책 이름
    @SerializedName("book") val book: ArrayList<CommentsBookName>,
    // 사용자가 쓴 책방 댓글 리스트
    @SerializedName("writing") val writing: ArrayList<CommentsWriting>
)