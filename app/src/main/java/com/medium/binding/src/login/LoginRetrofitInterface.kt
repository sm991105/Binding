package com.medium.binding.src.login

import com.medium.binding.src.login.models.LoginResponse
import com.medium.binding.src.login.models.PostLoginBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginRetrofitInterface {

    // 로그인 API
    @POST("/sign-in")
    fun postLogin(@Body params: PostLoginBody): Call<LoginResponse>

}