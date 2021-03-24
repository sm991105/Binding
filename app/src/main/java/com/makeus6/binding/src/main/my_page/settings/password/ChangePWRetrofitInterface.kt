package com.makeus6.binding.src.main.my_page.settings.password

import com.makeus6.binding.config.BaseResponse
import com.makeus6.binding.src.main.my_page.settings.models.PatchPWBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH


interface ChangePWRetrofitInterface {

    // 비밀번호 변경 API
    @PATCH("/users/password")
    fun changePW(@Body params: PatchPWBody): Call<BaseResponse>

}