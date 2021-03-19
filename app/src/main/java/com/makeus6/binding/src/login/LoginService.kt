package com.makeus6.binding.src.login

import com.makeus6.binding.config.ApplicationClass
import com.makeus6.binding.src.login.models.LoginResponse
import com.makeus6.binding.src.login.models.PostLoginBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginService(val view: LoginActivityView) {

    // 로그인 API 실행
    fun tryPostLogin(email: String, pwd: String){

        val loginBody = PostLoginBody(email, pwd)

        val loginRetrofitInterface = ApplicationClass.sRetrofit.create(
            LoginRetrofitInterface::class.java)

        loginRetrofitInterface.postLogin(loginBody)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(call: Call<LoginResponse>,
                                        response: Response<LoginResponse>
                ) {
                    view.onPostLoginSuccess(response.body() as LoginResponse)
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    view.onPostLoginFailure(t.message ?: "통신 오류")
                }
            })
    }
}