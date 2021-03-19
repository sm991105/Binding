package com.makeus6.binding.src.main.menu.store_detail

import com.makeus6.binding.config.BaseResponse
import com.makeus6.binding.src.main.menu.store_detail.models.GetBookStoreResponse

interface StoreDetailFragmentView {

    // 서점 상세 조회 API 콜백 함수
    fun onGetBookStoreSuccess(response: GetBookStoreResponse)

    fun onGetBookStoreFailure(message: String)

    // 북마크 수정 API 콜백 함수
    fun onPatchBookmarkSuccess(response: BaseResponse)

    fun onPatchBookmarkFailure(message: String)

}