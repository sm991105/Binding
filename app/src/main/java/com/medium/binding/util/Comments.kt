package com.medium.binding.util

import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseResponse
import com.medium.binding.src.main.home.models.CommentsBody
import com.medium.binding.src.main.home.models.ReportBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

class Comments {

    interface CommentsView{
        // 책방 글 삭제 Retrofit 콜백 함수
        fun onDeleteCommentsSuccess(response: BaseResponse)

        fun onDeleteCommentsFailure(message: String)

        // 책방 글 신고 Retrofit 콜백 함수
        fun onPostReportSuccess(response: BaseResponse)

        fun onPostReportFailure(message: String)

        // 책방 글 수정 콜백 함수
        fun onPatchCommentsSuccess(response: BaseResponse)

        fun onPatchCommentsFailure(message: String)
    }

    interface CommentsRetrofitInterface{
        // 책방 글 삭제
        @DELETE("/books/{bookIdx}/contents/{contentsIdx}")
        fun deleteComments(@Path("bookIdx") bookIdx: Int,
                           @Path("contentsIdx") contentsIdx: Int
        ): Call<BaseResponse>

        // 책방 글 신고
        @POST("/books/{bookIdx}/contents/{contentsIdx}/report-contents")
        fun postReport(@Path("bookIdx") bookIdx: Int,
                       @Path("contentsIdx") contentsIdx: Int,
                       @Body params: ReportBody
        ): Call<BaseResponse>

        // 책방 글 수정
        @PATCH("/books/{bookIdx}/contents/{contentsIdx}")
        fun patchComments(@Path("bookIdx") bookIdx: Int,
                          @Path("contentsIdx") contentsIdx: Int,
                          @Body params: CommentsBody
        ): Call<BaseResponse>
    }

    class CommentsService(val view: CommentsView) {
        // 책방 글 삭제 API 실행 (네트워크 통신)
        fun tryDeleteComments(bookIdx: Int, contentsIdx: Int){

            val commentsRetrofitInterface = ApplicationClass.sRetrofit.create(
                CommentsRetrofitInterface::class.java)

            commentsRetrofitInterface.deleteComments(bookIdx, contentsIdx)
                .enqueue(object : Callback<BaseResponse> {

                    override fun onResponse(call: Call<BaseResponse>,
                                            response: Response<BaseResponse>
                    ) {
                        view.onDeleteCommentsSuccess(response.body() as BaseResponse)
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        view.onDeleteCommentsFailure(t.message ?: "통신 오류")
                    }
                })
        }

        // 책방 글 신고 API 실행 (네트워크 통신)
        fun tryPostReport(bookIdx: Int, contentsIdx: Int, reportBody: ReportBody){

            val commentsRetrofitInterface = ApplicationClass.sRetrofit.create(
                CommentsRetrofitInterface::class.java)

            commentsRetrofitInterface.postReport(bookIdx, contentsIdx, reportBody)
                .enqueue(object : Callback<BaseResponse> {

                    override fun onResponse(call: Call<BaseResponse>,
                                            response: Response<BaseResponse>
                    ) {
                        view.onPostReportSuccess(response.body() as BaseResponse)
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        view.onPostReportFailure(t.message ?: "통신 오류")
                    }
                })
        }

        // 책방 글 수정 API 실행 (네트워크 통신)
        fun tryPatchComments(bookIdx: Int, contentsIdx: Int, commentsBody: CommentsBody){

            val commentsRetrofitInterface = ApplicationClass.sRetrofit.create(
                CommentsRetrofitInterface::class.java)

            commentsRetrofitInterface.patchComments(bookIdx, contentsIdx, commentsBody)
                .enqueue(object : Callback<BaseResponse> {

                    override fun onResponse(call: Call<BaseResponse>,
                                            response: Response<BaseResponse>
                    ) {
                        view.onPatchCommentsSuccess(response.body() as BaseResponse)
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        view.onPatchCommentsFailure(t.message ?: "통신 오류")
                    }
                })
        }
    }

    // 홈 or 프래그먼트에서 구현, 어댑터에 전달할 리스너
    interface ClickListener{
        // 삭제 요청
        fun onClickRemove(contentsIdx: Int)

        // 신고 요청
        fun onClickReport(reportReason: String, contentIdx: Int)

        // 발행,수정 요청
        // 책방 글 발행, 수정 버튼을 누르면 HomeCreateFragment, MyPostFragment에서 실행할 함수
        // commentsFlag - 발행: 0 , 수정: 1
        // 수정할 떄 contentsIdx가 필요하다
        fun onClickPub(comments: String, commentsFlag: Int, contentsIdx: Int)
    }

    // 각 클래스의 어댑터에서 구현, 다이얼로그에 전달할 리스너
    interface AdapterRemoveListener{
        fun onClickRemove()
    }

    interface AdapterReportListener {
        fun onClickReport(reportReason: String)
    }
}