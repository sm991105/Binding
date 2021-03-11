package com.example.binding.src.main.menu

import com.example.binding.config.ApplicationClass
import com.example.binding.src.login.LoginActivityView
import com.example.binding.src.login.LoginRetrofitInterface
import com.example.binding.src.login.models.LoginResponse
import com.example.binding.src.login.models.PostLoginBody
import com.example.binding.src.main.menu.models.GetStoresResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuService(val view: MenuFragmentView) {

    // 전체서점 API 실행
    fun tryGetAllStores(page: Int, limit: Int){

        val menuRetrofitInterface = ApplicationClass.sRetrofit.create(
            MenuRetrofitInterface::class.java)

        menuRetrofitInterface.getAllStores(page,limit)
            .enqueue(object : Callback<GetStoresResponse> {

                override fun onResponse(call: Call<GetStoresResponse>,
                                        response: Response<GetStoresResponse>
                ) {
                    view.onGetAllStoresSuccess(response.body() as GetStoresResponse)
                }

                override fun onFailure(call: Call<GetStoresResponse>, t: Throwable) {
                    view.onGetAllStoresFailure(t.message ?: "통신 오류")
                }
            })
    }
}