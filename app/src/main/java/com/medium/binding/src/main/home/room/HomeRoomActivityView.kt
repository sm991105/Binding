package com.medium.binding.src.main.home.room

import android.view.View
import com.medium.binding.config.BaseResponse
import com.medium.binding.src.main.home.models.CommentsBody
import com.medium.binding.src.main.home.models.GetCommentsResponse

interface HomeRoomActivityView {

    // 최신순 책방 댓글 콜백 함수
    fun onGetNewestWRSuccess(response: GetCommentsResponse)

    fun onGetNewestWRFailure(message: String)

    // 북마크순 책방 댓글 콜백 함수
    fun onGetMarkedWRSuccess(response: GetCommentsResponse)

    fun onGetMarkedWRFailure(message: String)

    // 북마크순 책방 댓글 콜백 함수
    fun onPatchWBookmarkSuccess(response: BaseResponse, itemPos: Int)

    fun onPatchWBookmarkFailure(message: String)

    // 책방 글 발행 콜백 함수
    fun onPostCommentsSuccess(response: BaseResponse)

    fun onPostCommentsFailure(message: String)

    // 책방 글 수정 콜백 함수
    fun onPatchCommentsSuccess(response: BaseResponse)

    fun onPatchCommentsFailure(message: String)

    // 책방 글 발행, 수정할 때, HomeCreateFragment에서 발행 버튼 누를 때 호출할 함수
    // commentsFlag - 발행: 0 , 수정: 1
    // 수정할 떄 contentsIdx가 필요하다
    fun onClickPub(commentsBody: CommentsBody, commentsFlag: Int, contentsIdx: Int)

    // 책방 글 삭제 Retrofit 콜백 함수
    fun onDeleteCommentsSuccess(response: BaseResponse)

    fun onDeleteCommentsFailure(message: String)

    // 책방 글 삭제 버튼 콜백 함수
    fun confirmRemove(contentsIdx: Int)

    // 책방 글 삭제 Retrofit 콜백 함수
    fun onPostReportSuccess(response: BaseResponse)

    fun onPostReportFailure(message: String)
}