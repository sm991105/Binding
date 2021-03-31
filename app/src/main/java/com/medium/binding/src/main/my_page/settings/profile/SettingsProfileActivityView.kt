package com.medium.binding.src.main.my_page.settings.profile

import com.medium.binding.config.BaseResponse
import com.medium.binding.src.main.my_page.settings.models.GetProfileResponse

interface SettingsProfileActivityView {

    // 프로필 조회 콜백 함수
    fun onGetProfileSuccess(response: GetProfileResponse)

    fun onGetProfileFailure(message: String)


    // 프로필 변경 콜백 함수
    fun onPatchProfileSuccess(response: BaseResponse)

    fun onPatchProfileFailure(message: String)
}