package com.example.binding.src.main.menu.store_detail

import com.example.binding.config.BaseResponse
import com.example.binding.src.join.join3.models.PostJoinBody
import com.example.binding.src.main.menu.models.GetStoresResponse
import com.example.binding.src.main.menu.store_detail.models.GetBookStoreResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StoreDetailRetrofitInterface {

    // 클릭한 서점 상세조회 api
    @GET("/bookstores")
    fun getBookStore(@Query("bookstoreIdx") bookstoreIdx: Int): Call<GetBookStoreResponse>
}