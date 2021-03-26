package com.makeus6.binding.src.main.home.create_room

import com.makeus6.binding.config.BaseResponse
import com.makeus6.binding.src.main.home.create_room.models.CreateBookBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CreateBookRetrofitInterface {

    // 책방 만들기 api
    @POST("/books")
    fun postBook(@Body params: CreateBookBody): Call<BaseResponse>
}