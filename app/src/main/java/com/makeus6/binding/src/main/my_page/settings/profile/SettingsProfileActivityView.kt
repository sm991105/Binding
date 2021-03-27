package com.makeus6.binding.src.main.my_page.settings.profile

import com.makeus6.binding.config.BaseResponse
import com.makeus6.binding.src.main.my_page.settings.models.GetProfileResponse

interface SettingsProfileActivityView {

    // 프로필 조회 콜백 함수
    fun onGetProfileSuccess(response: GetProfileResponse)

    fun onGetProfileFailure(message: String)

    // 프로필 사진 변경 콜백 함수
    fun onPatchImgSuccess(response: BaseResponse)

    fun onPatchImgFailure(message: String)

    // 프로필 사진 삭제 콜백 함수
    fun onDeleteImgSuccess(response: BaseResponse)

    fun onDeleteImgFailure(message: String)

    // 닉네임 변경 콜백 함수
    fun onPatchNicknameSuccess(response: BaseResponse)

    fun onPatchNicknameFailure(message: String)

    // 프로필 변경 콜백 함수
    fun onPatchProfileSuccess(response: BaseResponse)

    fun onPatchProfileFailure(message: String)
}