package com.makeus6.binding.src.find_password

import com.makeus6.binding.config.BaseResponse
import com.makeus6.binding.src.find_password.models.PostFindPwBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FindPasswordRetrofitInterface {

    // 비밀번호 찾기 api
    @POST("/find-password")
    fun postFindPw(@Body params: PostFindPwBody): Call<BaseResponse>
}