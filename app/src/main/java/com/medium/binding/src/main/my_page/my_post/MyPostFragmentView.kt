package com.medium.binding.src.main.my_page.my_post

import com.medium.binding.src.main.my_page.models.UserCommentsResponse

interface MyPostFragmentView {

    // 유저 쓴 글 조회 콜백 함수
    fun onGetUserCommentsSuccess(response: UserCommentsResponse)

    fun onGetUserCommentsFailure(message: String)
}