package com.makeus6.binding.src.main.menu.store_detail

import com.makeus6.binding.config.BaseResponse
import com.makeus6.binding.src.main.menu.store_detail.models.GetBookStoreResponse
import retrofit2.Call
import retrofit2.http.*

interface StoreDetailRetrofitInterface {

    // 클릭한 서점 상세조회 api
    @GET("/bookstores/{bookstoreIdx}")
    fun getBookStore(@Path("bookstoreIdx") bookstoreIdx: Int): Call<GetBookStoreResponse>

    // 북마크 수정 api
    @PATCH("/bookmark/bookstores/{bookstoreIdx}")
    fun patchBookmark(@Path("bookstoreIdx") bookstoreIdx: Int): Call<BaseResponse>
}