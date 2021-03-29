package com.medium.binding.src.join.join1

import com.medium.binding.config.BaseResponse
import retrofit2.Call
import retrofit2.http.*

interface Join1RetrofitInterface {

    // 이메일 중복, 형식 검사 api
    @GET("/check-email")
    fun getCheckEmail(@Query("email") email: String): Call<BaseResponse>
}