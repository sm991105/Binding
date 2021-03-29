package com.medium.binding.src.find_password

import com.medium.binding.config.BaseResponse
import com.medium.binding.src.find_password.models.PostFindPwBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FindPasswordRetrofitInterface {

    // 비밀번호 찾기 api
    @POST("/find-password")
    fun postFindPw(@Body params: PostFindPwBody): Call<BaseResponse>
}