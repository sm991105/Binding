package com.example.binding.src.main.menu

import com.example.binding.config.BaseResponse
import com.example.binding.src.join.join3.models.PostJoinBody
import com.example.binding.src.main.menu.models.GetStoresResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MenuRetrofitInterface {

    // 전체 서점 가져오기 api
    @GET("/bookstores/all")
    fun getAllStores(@Query("page") page: Int,
                 @Query("limit") limit: Int
    ): Call<GetStoresResponse>

    // 특정 지역 서점
    @GET("/bookstores")
    fun getLocationStores(@Query("page") page: Int,
                  @Query("limit") limit: Int,
                   @Query("locationfilter") locationFilter: ArrayList<String>
    ): Call<GetStoresResponse>
}