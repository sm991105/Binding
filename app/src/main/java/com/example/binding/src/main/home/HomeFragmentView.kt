package com.example.binding.src.main.home

import com.example.binding.src.main.home.models.GetNewestResponse
import com.example.binding.src.main.home.models.GetPopularResponse

interface HomeFragmentView {

    // 최신순 책방조회 콜백 함수
    fun onGetNewestSuccess(response: GetNewestResponse)

    fun onGetNewestFailure(message: String)

    // 인기순 책방조회 콜백 함수
    fun onGetPopularSuccess(response: GetPopularResponse)

    fun onGetPopularFailure(message: String)
}