package com.medium.binding.src.main.my_page

import com.medium.binding.config.ApplicationClass
import com.medium.binding.src.main.my_page.models.GetUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageService(val view: MyPageFragmentView) {

    // 유저 프로필 조회 함수(네트워크 통신)
    fun tryGetUser(){

        val myPageRetrofitInterface = ApplicationClass.sRetrofit.create(
            MyPageRetrofitInterface::class.java)

        myPageRetrofitInterface.getUser()
            .enqueue(object : Callback<GetUserResponse> {

                override fun onResponse(call: Call<GetUserResponse>,
                                        response: Response<GetUserResponse>
                ) {
                    view.onGetUserSuccess(response.body() as GetUserResponse)
                }

                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                    view.onGetUserFailure(t.message ?: "통신 오류")
                }
            })
    }
}