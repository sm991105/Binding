package com.example.binding.src.main.home

import com.example.binding.config.BaseResponse

interface HomeFragmentView {

    // 최신순 책방조회 콜백 함수

    fun onGetNewestSuccess(response: BaseResponse)

    fun onGetNewestFailure(message: String)

    // 인기순 책방조회 콜백 함수
    fun onGetPopularSuccess(response: BaseResponse)

    fun onGetPopularFailure(message: String)
}