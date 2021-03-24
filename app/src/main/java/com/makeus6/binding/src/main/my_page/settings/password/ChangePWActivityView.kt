package com.makeus6.binding.src.main.my_page.settings.password

import com.makeus6.binding.config.BaseResponse

interface ChangePWActivityView {

    // 비밀번호 변경 콜백 함수
    fun onPatchPWSuccess(response: BaseResponse)

    fun onPatchPWFailure(message: String)
}