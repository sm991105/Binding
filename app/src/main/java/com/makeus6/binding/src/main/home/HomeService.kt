package com.makeus6.binding.src.main.home

import com.makeus6.binding.config.ApplicationClass
import com.makeus6.binding.src.main.home.models.GetNewestResponse
import com.makeus6.binding.src.main.home.models.GetPopularResponse
import com.makeus6.binding.src.main.home.models.GetSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeService(val view: HomeFragmentView) {

    // 최신순 책방 불러오기 API 실행 (네트워크 통신)
    fun tryGetNewest(){

        val homeRetrofitInterface = ApplicationClass.sRetrofit.create(
            HomeRetrofitInterface::class.java)

        homeRetrofitInterface.getNewest(0, 20)
            .enqueue(object : Callback<GetNewestResponse> {

                override fun onResponse(call: Call<GetNewestResponse>,
                                        response: Response<GetNewestResponse>
                ) {
                    view.onGetNewestSuccess(response.body() as GetNewestResponse)
                }

                override fun onFailure(call: Call<GetNewestResponse>, t: Throwable) {
                    view.onGetNewestFailure(t.message ?: "통신 오류")
                }
            })
    }

    // 인기순 책방 불러오기 API 실행
    fun tryGetPopular(){

        val homeRetrofitInterface = ApplicationClass.sRetrofit.create(
            HomeRetrofitInterface::class.java)

        homeRetrofitInterface.getPopular(0, 20)
            .enqueue(object : Callback<GetPopularResponse> {

                override fun onResponse(call: Call<GetPopularResponse>,
                                        response: Response<GetPopularResponse>
                ) {
                    view.onGetPopularSuccess(response.body() as GetPopularResponse)
                }

                override fun onFailure(call: Call<GetPopularResponse>, t: Throwable) {
                    view.onGetPopularFailure(t.message ?: "통신 오류")
                }
            })
    }

    // 책방 검색 API 실행 (네트워크 통신)
    fun tryGetSearchBooks(bookName: String){

        val homeRetrofitInterface = ApplicationClass.sRetrofit.create(
            HomeRetrofitInterface::class.java)

        homeRetrofitInterface.getSearchBooks(bookName,0 , 20)
            .enqueue(object : Callback<GetSearchResponse> {

                override fun onResponse(call: Call<GetSearchResponse>,
                                        response: Response<GetSearchResponse>
                ) {
                    view.onGetSearchSuccess(response.body() as GetSearchResponse, bookName)
                }

                override fun onFailure(call: Call<GetSearchResponse>, t: Throwable) {
                    view.onGetSearchFailure(t.message ?: "통신 오류")
                }
            })
    }
}