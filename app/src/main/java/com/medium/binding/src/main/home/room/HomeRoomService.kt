package com.medium.binding.src.main.home.room

import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseResponse
import com.medium.binding.src.main.home.models.GetCommentsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRoomService(val view: HomeRoomActivityView) {

    // 최신순 책방 댓글 불러오기 API 실행 (네트워크 통신)
    fun tryGetNewestWR(bookIdx: Int){

        val homeRoomRetrofitInterface = ApplicationClass.sRetrofit.create(
            HomeRoomRetrofitInterface::class.java)

        homeRoomRetrofitInterface.getNewestWR(bookIdx, 0, 20)
            .enqueue(object : Callback<GetCommentsResponse> {

                override fun onResponse(call: Call<GetCommentsResponse>,
                                        response: Response<GetCommentsResponse>
                ) {
                    view.onGetNewestWRSuccess(response.body() as GetCommentsResponse)
                }

                override fun onFailure(call: Call<GetCommentsResponse>, t: Throwable) {
                    view.onGetNewestWRFailure(t.message ?: "통신 오류")
                }
            })
    }

    // 최신순 책방 댓글 불러오기 API 실행 (네트워크 통신)
    fun tryGetMarkedWR(bookIdx: Int){

        val homeRoomRetrofitInterface = ApplicationClass.sRetrofit.create(
            HomeRoomRetrofitInterface::class.java)

        homeRoomRetrofitInterface.getMarkedWR(bookIdx,0, 20)
            .enqueue(object : Callback<GetCommentsResponse> {

                override fun onResponse(call: Call<GetCommentsResponse>,
                                        response: Response<GetCommentsResponse>
                ) {
                    view.onGetMarkedWRSuccess(response.body() as GetCommentsResponse)
                }

                override fun onFailure(call: Call<GetCommentsResponse>, t: Throwable) {
                    view.onGetMarkedWRFailure(t.message ?: "통신 오류")
                }
            })
    }

    // 최신순 책방 댓글 불러오기 API 실행 (네트워크 통신)
    fun tryPatchWBookmark(contentsIdx: Int, itemPos: Int){

        val homeRoomRetrofitInterface = ApplicationClass.sRetrofit.create(
            HomeRoomRetrofitInterface::class.java)

        homeRoomRetrofitInterface.patchWBookmark(contentsIdx)
            .enqueue(object : Callback<BaseResponse> {

                override fun onResponse(call: Call<BaseResponse>,
                                        response: Response<BaseResponse>
                ) {
                    view.onPatchWBookmarkSuccess(response.body() as BaseResponse, itemPos)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onPatchWBookmarkFailure(t.message ?: "통신 오류")
                }
            })
    }
}