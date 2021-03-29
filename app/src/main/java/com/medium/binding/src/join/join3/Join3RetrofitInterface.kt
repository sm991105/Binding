package com.medium.binding.src.join.join3

import com.medium.binding.config.BaseResponse
import com.medium.binding.src.join.join3.models.PostJoinBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Join3RetrofitInterface {

    // 회원가입 요청 api
    @POST("/sign-up")
    fun postJoin(@Body params: PostJoinBody): Call<BaseResponse>
}