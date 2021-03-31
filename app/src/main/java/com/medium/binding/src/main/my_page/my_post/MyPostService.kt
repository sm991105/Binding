package com.medium.binding.src.main.my_page.my_post

import com.medium.binding.config.ApplicationClass
import com.medium.binding.src.main.my_page.models.UserCommentsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostService(val view: MyPostFragmentView) {

    // 유저가 쓴 글 상세 조회 함수(네트워크 통신)
    fun tryGetUserComments(bookIdx: Int){

        val myPostRetrofitInterface = ApplicationClass.sRetrofit.create(
            MyPostRetrofitInterface::class.java)

        myPostRetrofitInterface.getUserComments(bookIdx,0,50)
            .enqueue(object : Callback<UserCommentsResponse> {

                override fun onResponse(call: Call<UserCommentsResponse>,
                                        response: Response<UserCommentsResponse>
                ) {
                    view.onGetUserCommentsSuccess(response.body() as UserCommentsResponse)
                }

                override fun onFailure(call: Call<UserCommentsResponse>, t: Throwable) {
                    view.onGetUserCommentsFailure(t.message ?: "통신 오류")
                }
            })
    }
}