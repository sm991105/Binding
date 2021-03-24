package com.makeus6.binding.src.main.my_page.settings.profile

import com.makeus6.binding.config.ApplicationClass
import com.makeus6.binding.src.main.my_page.settings.models.GetProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsProfileService(val view: SettingsProfileActivityView) {

    // 유저 프로필 조회 함수(네트워크 통신)
    fun tryGetProfile(){

        val settingsProfileRetrofitInterface = ApplicationClass.sRetrofit.create(
            SettingsProfileRetrofitInterface::class.java)

        settingsProfileRetrofitInterface.getProfile()
            .enqueue(object : Callback<GetProfileResponse> {

                override fun onResponse(call: Call<GetProfileResponse>,
                                        response: Response<GetProfileResponse>
                ) {
                    view.onGetProfileSuccess(response.body() as GetProfileResponse)
                }

                override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                    view.onGetProfileFailure(t.message ?: "통신 오류")
                }
            })
    }
}