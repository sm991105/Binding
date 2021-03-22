package com.makeus6.binding.src.main.my_page

import com.makeus6.binding.src.main.my_page.models.GetUserResponse

interface MyPageFragmentView {

    // 프로필 조회 콜백 함수
    fun onGetUserSuccess(response: GetUserResponse)

    fun onGetUserFailure(message: String)
}