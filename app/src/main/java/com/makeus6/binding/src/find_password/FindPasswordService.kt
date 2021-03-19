package com.makeus6.binding.src.find_password

import com.makeus6.binding.config.ApplicationClass
import com.makeus6.binding.config.BaseResponse
import com.makeus6.binding.src.find_password.models.PostFindPwBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindPasswordService(val view: FindPasswordActivityView) {

    // 비밀번호 찾기 API
    fun tryPostFindPw(email: String, nickname: String){

        val findPwBody = PostFindPwBody(email, nickname)

        val findPasswordRetrofitInterface = ApplicationClass.sRetrofit.create(
            FindPasswordRetrofitInterface::class.java)

        findPasswordRetrofitInterface.postFindPw(findPwBody)
            .enqueue(object : Callback<BaseResponse> {

                override fun onResponse(call: Call<BaseResponse>,
                                        response: Response<BaseResponse>
                ) {
                    view.onPostFindPwSuccess(response.body() as BaseResponse)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onPostFindPwFailure(t.message ?: "통신 오류")
                }
            })
    }
}