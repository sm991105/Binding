package com.medium.binding.src.main.my_page.models

import com.medium.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

// MyPageRetrofitInterface - getUserComments의 response 데이터 클래스
data class UserCommentsResponse (
    @SerializedName("result") val result: CommentsResult
): BaseResponse()