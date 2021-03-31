package com.medium.binding.src.main.my_page.my_post

import com.medium.binding.src.main.my_page.models.GetUserResponse
import com.medium.binding.src.main.my_page.models.UserCommentsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MyPostRetrofitInterface {

    // 유저가 쓴 글 클릭 -> 상세 조회 API, jwt만 헤더값으로 넘겨주면 된다
    @GET("/users-writing/books/{bookIdx}")
    fun getUserComments(
        @Path("bookIdx") bookIdx: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<UserCommentsResponse>

    // 유저가 북마크한 글 클릭 -> 상세 조회 API, jwt만 헤더값으로 넘겨주면 된다
    @GET("/users-bookmark/books/{bookIdx}")
    fun getUserMarkComments(
        @Path("bookIdx") bookIdx: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<GetUserResponse>
}