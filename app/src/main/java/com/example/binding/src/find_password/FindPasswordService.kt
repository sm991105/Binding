package com.example.binding.src.find_password

import com.example.binding.config.ApplicationClass
import com.example.binding.config.BaseResponse
import com.example.binding.src.find_password.models.PostFindPwBody
import com.example.binding.src.login.LoginActivityView
import com.example.binding.src.login.LoginRetrofitInterface
import com.example.binding.src.login.models.LoginResponse
import com.example.binding.src.login.models.PostLoginBody
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