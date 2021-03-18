package com.example.binding.src.main.home

import com.example.binding.config.BaseResponse
import com.example.binding.src.join.join3.models.PostJoinBody
import com.example.binding.src.main.home.models.GetNewestResponse
import com.example.binding.src.main.home.models.GetPopularResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HomeRetrofitInterface {

    // 책방 최신순 api
    @GET("/books/newest-books")
    fun getNewest(@Query("page") page: Int,
                 @Query("limit") limit: Int
    ): Call<GetNewestResponse>

    // 책방 인기순 api
    @GET("/books/popularity-books")
    fun getPopular(@Query("page") page: Int,
                  @Query("limit") limit: Int
    ): Call<GetPopularResponse>
}