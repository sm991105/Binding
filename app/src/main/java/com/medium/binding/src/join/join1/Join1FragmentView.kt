package com.medium.binding.src.join.join1

import com.medium.binding.config.BaseResponse

interface Join1FragmentView {

    // 이메일 중복 검사 성공, 실패시 콜백 함수

    fun onGetCheckEmailSuccess(response: BaseResponse, email: String)

    fun onGetCheckEmailFailure(message: String)
}