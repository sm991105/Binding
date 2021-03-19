package com.makeus6.binding.src.join.join3

import com.makeus6.binding.config.BaseResponse

interface Join3FragmentView {

    // 회원가입 요청 api 콜백 함수

    fun onPostJoinSuccess(response: BaseResponse)

    fun onPostJoinFailure(message: String)
}