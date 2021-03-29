package com.medium.binding.src.join.join1

import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Join1Service(val view: Join1FragmentView) {

    // 서버한테 이메일 중복 검사하는 API 실행
    fun tryGetCheckEmail(email: String){

        val join1RetrofitInterface = ApplicationClass.sRetrofit.create(
            Join1RetrofitInterface::class.java)

        join1RetrofitInterface.getCheckEmail(email)
            .enqueue(object : Callback<BaseResponse> {

                override fun onResponse(call: Call<BaseResponse>,
                                        response: Response<BaseResponse>
                ) {
                    view.onGetCheckEmailSuccess(response.body() as BaseResponse, email)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onGetCheckEmailFailure(t.message ?: "통신 오류")
                }
            })
    }
}