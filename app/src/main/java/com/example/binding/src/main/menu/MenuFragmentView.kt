package com.example.binding.src.main.menu

import com.example.binding.src.main.menu.models.GetStoresResponse

interface MenuFragmentView {

    // 전체 서점 API 콜백 함수
    fun onGetAllStoresSuccess(response: GetStoresResponse)

    fun onGetAllStoresFailure(message: String)
}