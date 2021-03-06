package com.example.binding.src.login

import com.example.binding.src.login.models.LoginResponse

interface LoginActivityView {

    // 로그인 API 콜백 함수
    fun onPostLoginSuccess(response: LoginResponse)

    fun onPostLoginFailure(message: String)
}