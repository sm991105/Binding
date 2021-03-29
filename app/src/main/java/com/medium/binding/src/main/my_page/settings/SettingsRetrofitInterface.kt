package com.medium.binding.src.main.my_page.settings

import com.medium.binding.config.BaseResponse
import retrofit2.Call
import retrofit2.http.PATCH

interface SettingsRetrofitInterface {

    // 유저 프로필 조회 API, jwt만 헤더값으로 넘겨주면 된다
    @PATCH("/users/status")
    fun patchWithdraw(): Call<BaseResponse>
}