package com.medium.binding.src.main.home.create_room

import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseResponse
import com.medium.binding.src.main.home.create_room.models.CreateBookBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateBookService(val view: CreateBookView) {

    // 책방 만들기 API 실행 (네트워크 통신)
    fun tryPostBook(bookBody: CreateBookBody){

        val createBookRetrofitInterface = ApplicationClass.sRetrofit.create(
            CreateBookRetrofitInterface::class.java)

        createBookRetrofitInterface.postBook(bookBody)
            .enqueue(object : Callback<BaseResponse> {

                override fun onResponse(call: Call<BaseResponse>,
                                        response: Response<BaseResponse>
                ) {
                    view.onPostBookSuccess(response.body() as BaseResponse)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onPostBookFailure(t.message ?: "통신 오류")
                }
            })
    }
}