package com.medium.binding.src.main.home.room

import com.medium.binding.config.BaseResponse
import com.medium.binding.src.main.home.models.CommentsBody
import com.medium.binding.src.main.home.models.GetCommentsResponse
import retrofit2.Call
import retrofit2.http.*

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

    // 책방 글 발행
    @POST("/books/{bookIdx}/contents")
    fun postComments(@Path("bookIdx") bookIdx: Int,
                     @Body params: CommentsBody
    ): Call<BaseResponse>

    // 책방 글 수정
    @PATCH("/books/{bookIdx}/contents/{contentsIdx}")
    fun patchComments(@Path("bookIdx") bookIdx: Int,
                     @Path("contentsIdx") contentsIdx: Int,
                     @Body params: CommentsBody
    ): Call<BaseResponse>

    // 책방 글 삭제
    @DELETE("/books/{bookIdx}/contents/{contentsIdx}")
    fun deleteComments(@Path("bookIdx") bookIdx: Int,
                      @Path("contentsIdx") contentsIdx: Int
    ): Call<BaseResponse>
}