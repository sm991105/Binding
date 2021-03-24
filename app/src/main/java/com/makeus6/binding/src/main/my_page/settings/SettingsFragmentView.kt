package com.makeus6.binding.src.main.my_page.settings

import com.makeus6.binding.config.BaseResponse

interface SettingsFragmentView {

    // 프로필 조회 콜백 함수
    fun onPatchWithdrawSuccess(response: BaseResponse)

    fun onPatchWithdrawFailure(message: String)
}