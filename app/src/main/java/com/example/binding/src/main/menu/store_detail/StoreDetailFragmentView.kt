package com.example.binding.src.main.menu.store_detail

import com.example.binding.src.main.menu.store_detail.models.GetBookStoreResponse

interface StoreDetailFragmentView {

    // 서점 상세 조회 API 콜백 함수
    fun onGetBookStoreSuccess(response: GetBookStoreResponse)

    fun onGetBookStoreFailure(message: String)

}