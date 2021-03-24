package com.makeus6.binding.src.main.my_page.settings.profile

import com.makeus6.binding.src.main.my_page.settings.models.GetProfileResponse
import retrofit2.Call
import retrofit2.http.GET


interface SettingsProfileRetrofitInterface {

    // 유저 프로필 조회 API, jwt만 헤더값으로 넘겨주면 된다
    @GET("/users/profile")
    fun getProfile(): Call<GetProfileResponse>

}