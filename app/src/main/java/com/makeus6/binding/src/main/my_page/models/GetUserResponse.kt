package com.makeus6.binding.src.main.my_page.models

import com.makeus6.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName
import com.makeus6.binding.src.main.home.models.NewestResult

// 레트로핏 getUser의 response 데이터 클래스
data class GetUserResponse (
    @SerializedName("result") val result: UserResult
): BaseResponse()