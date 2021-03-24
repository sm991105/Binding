package com.makeus6.binding.src.main.my_page.settings

import com.makeus6.binding.config.ApplicationClass
import com.makeus6.binding.config.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsService(val view: SettingsFragmentView) {

    // 유저 프로필 조회 함수(네트워크 통신)
    fun tryPatchWithdraw(){

        val settingsRetrofitInterface = ApplicationClass.sRetrofit.create(
            SettingsRetrofitInterface::class.java)

        settingsRetrofitInterface.patchWithdraw()
            .enqueue(object : Callback<BaseResponse> {

                override fun onResponse(call: Call<BaseResponse>,
                                        response: Response<BaseResponse>
                ) {
                    view.onPatchWithdrawSuccess(response.body() as BaseResponse)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onPatchWithdrawFailure(t.message ?: "통신 오류")
                }
            })
    }
}