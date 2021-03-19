package com.makeus6.binding.src.main.home

import com.makeus6.binding.src.main.home.models.GetNewestResponse
import com.makeus6.binding.src.main.home.models.GetPopularResponse
import com.makeus6.binding.src.main.home.models.GetSearchResponse
import retrofit2.Call
import retrofit2.http.GET
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


    // 책방 검색 api
    @GET("/books?bookName")
    fun getSearchBooks(@Query("bookName") bookName: String): Call<GetSearchResponse>

}