package com.makeus6.binding.src.main.my_page

import com.makeus6.binding.src.main.my_page.models.GetUserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MyPageRetrofitInterface {

    // 유저 프로필 조회 API, jwt만 헤더값으로 넘겨주면 된다
    @GET("/users")
    fun getUser(): Call<GetUserResponse>

}