package com.makeus6.binding.src.main.my_page.settings.profile

import com.makeus6.binding.src.main.my_page.settings.models.GetProfileResponse

interface SettingsProfileActivityView {

    // 프로필 조회 콜백 함수
    fun onGetProfileSuccess(response: GetProfileResponse)

    fun onGetProfileFailure(message: String)
}