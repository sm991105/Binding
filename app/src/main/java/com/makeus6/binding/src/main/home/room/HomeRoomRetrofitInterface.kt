package com.makeus6.binding.src.main.home.room

import com.makeus6.binding.config.BaseResponse
import com.makeus6.binding.src.main.home.models.GetCommentsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeRoomRetrofitInterface {

    // 책방 댓글 최신순 api
    @GET("/books/{bookIdx}/newest-books")
    fun getNewestWR(@Path("bookIdx") bookIdx: Int,
                    @Query("page") page: Int,
                    @Query("limit") limit: Int
    ): Call<GetCommentsResponse>

    // 책방 댓글 북마크순 api
    @GET("/books/{bookIdx}/bookmark-books")
    fun getMarkedWR(@Path("bookIdx") bookIdx: Int,
                    @Query("page") page: Int,
                    @Query("limit") limit: Int
    ): Call<GetCommentsResponse>

    // 댓글 북마크 상태 수정
    @PATCH("/contents/{contentsIdx}/bookmark")
    fun patchWBookmark(@Path("contentsIdx") contentsIdx: Int): Call<BaseResponse>
}