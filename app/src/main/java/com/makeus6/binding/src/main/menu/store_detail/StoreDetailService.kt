package com.makeus6.binding.src.main.menu.store_detail

import com.makeus6.binding.config.ApplicationClass
import com.makeus6.binding.config.BaseResponse
import com.makeus6.binding.src.main.menu.store_detail.models.GetBookStoreResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreDetailService(val view: StoreDetailFragmentView) {

    // 서점 상세조회 API 실행 (네트워크 통신)
    fun tryGetBookStore(bookstoreIdx: Int){

        val storeDetailRetrofitInterface = ApplicationClass.sRetrofit.create(
            StoreDetailRetrofitInterface::class.java)

        storeDetailRetrofitInterface.getBookStore(bookstoreIdx)
            .enqueue(object : Callback<GetBookStoreResponse> {

                override fun onResponse(call: Call<GetBookStoreResponse>,
                                        response: Response<GetBookStoreResponse>
                ) {
                    view.onGetBookStoreSuccess(response.body() as GetBookStoreResponse)
                }

                override fun onFailure(call: Call<GetBookStoreResponse>, t: Throwable) {
                    view.onGetBookStoreFailure(t.message ?: "통신 오류")
                }
            })
    }

    // 북마크 수정 API 실행 (네트워크 통신)
    fun tryPatchBookmark(bookstoreIdx: Int){

        val storeDetailRetrofitInterface = ApplicationClass.sRetrofit.create(
            StoreDetailRetrofitInterface::class.java)

        storeDetailRetrofitInterface.patchBookmark(bookstoreIdx)
            .enqueue(object : Callback<BaseResponse> {

                override fun onResponse(call: Call<BaseResponse>,
                                        response: Response<BaseResponse>
                ) {
                    view.onPatchBookmarkSuccess(response.body() as BaseResponse)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onPatchBookmarkFailure(t.message ?: "통신 오류")
                }
            })
    }
}