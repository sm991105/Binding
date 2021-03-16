package com.example.binding.src.find_password

import com.example.binding.config.BaseResponse
import com.example.binding.src.find_password.models.PostFindPwBody
import com.example.binding.src.login.models.LoginResponse
import com.example.binding.src.login.models.PostLoginBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FindPasswordRetrofitInterface {

    // 비밀번호 찾기 api
    @POST("/find-password")
    fun postFindPw(@Body params: PostFindPwBody): Call<BaseResponse>
}