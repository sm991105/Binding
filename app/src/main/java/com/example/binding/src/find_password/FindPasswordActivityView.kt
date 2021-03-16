package com.example.binding.src.find_password

import com.example.binding.config.BaseResponse

interface FindPasswordActivityView {

    // 로그인 API 콜백 함수
    fun onPostFindPwSuccess(response: BaseResponse)

    fun onPostFindPwFailure(message: String)
}