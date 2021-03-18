package com.example.binding.src.main.home

import com.example.binding.config.ApplicationClass
import com.example.binding.config.BaseResponse
import com.example.binding.src.login.LoginActivityView
import com.example.binding.src.login.LoginRetrofitInterface
import com.example.binding.src.login.models.LoginResponse
import com.example.binding.src.login.models.PostLoginBody
import com.example.binding.src.main.home.models.GetNewestResponse
import com.example.binding.src.main.home.models.GetPopularResponse
import com.example.binding.src.main.menu.MenuFragmentView
import com.example.binding.src.main.menu.MenuRetrofitInterface
import com.example.binding.src.main.menu.models.GetStoresResponse
import com.example.binding.src.main.menu.store_detail.StoreDetailFragmentView
import com.example.binding.src.main.menu.store_detail.StoreDetailRetrofitInterface
import com.example.binding.src.main.menu.store_detail.models.GetBookStoreResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeService(val view: HomeFragmentView) {

    // 서점 상세조회 API 실행 (네트워크 통신)
    fun tryGetNewest(){

        val homeRetrofitInterface = ApplicationClass.sRetrofit.create(
            HomeRetrofitInterface::class.java)

        homeRetrofitInterface.getNewest(1, 20)
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

    // 북마크 수정 API 실행 (네트워크 통신)
    fun tryGetPopular(){

        val homeRetrofitInterface = ApplicationClass.sRetrofit.create(
            HomeRetrofitInterface::class.java)

        homeRetrofitInterface.getPopular(1, 20)
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
}