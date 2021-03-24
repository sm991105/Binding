package com.makeus6.binding.src.main.my_page.settings.password

import com.makeus6.binding.config.ApplicationClass
import com.makeus6.binding.config.BaseResponse
import com.makeus6.binding.src.main.my_page.settings.models.PatchPWBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePWService(val view: ChangePWActivityView) {

    // 비밀번호 변경 요청 함수(네트워크 통신)
    fun tryPatchPW(patchPWBody: PatchPWBody){

        val changePWRetrofitInterface = ApplicationClass.sRetrofit.create(
            ChangePWRetrofitInterface::class.java)

        changePWRetrofitInterface.changePW(patchPWBody)
            .enqueue(object : Callback<BaseResponse> {

                override fun onResponse(call: Call<BaseResponse>,
                                        response: Response<BaseResponse>
                ) {
                    view.onPatchPWSuccess(response.body() as BaseResponse)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onPatchPWFailure(t.message ?: "통신 오류")
                }
            })
    }
}