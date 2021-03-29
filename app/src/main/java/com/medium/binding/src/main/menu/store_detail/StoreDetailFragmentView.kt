package com.medium.binding.src.main.menu.store_detail

import com.medium.binding.config.BaseResponse
import com.medium.binding.src.main.menu.store_detail.models.GetBookStoreResponse

interface StoreDetailFragmentView {

    // 서점 상세 조회 API 콜백 함수
    fun onGetBookStoreSuccess(response: GetBookStoreResponse)

    fun onGetBookStoreFailure(message: String)

    // 북마크 수정 API 콜백 함수
    fun onPatchBookmarkSuccess(response: BaseResponse)

    fun onPatchBookmarkFailure(message: String)

}