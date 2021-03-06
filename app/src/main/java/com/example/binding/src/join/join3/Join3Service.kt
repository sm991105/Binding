package com.example.binding.src.join.join3

import com.example.binding.config.ApplicationClass
import com.example.binding.config.BaseResponse
import com.example.binding.src.join.join3.models.PostJoinBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Join3Service(val view: Join3FragmentView) {

    // 서버에 회원가입 요청하는 API 호출
    fun tryPostJoin(joinBody: PostJoinBody){

        val join3RetrofitInterface = ApplicationClass.sRetrofit.create(
            Join3RetrofitInterface::class.java)

        join3RetrofitInterface.postJoin(joinBody)
            .enqueue(object : Callback<BaseResponse> {

                override fun onResponse(call: Call<BaseResponse>,
                                        response: Response<BaseResponse>
                ) {
                    view.onPostJoinSuccess(response.body() as BaseResponse)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onPostJoinFailure(t.message ?: "통신 오류")
                }
            })
    }
}