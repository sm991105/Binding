package com.makeus6.binding.src.main.home

import com.makeus6.binding.src.main.home.models.GetNewestResponse
import com.makeus6.binding.src.main.home.models.GetPopularResponse
import com.makeus6.binding.src.main.home.models.GetSearchResponse

interface HomeFragmentView {

    // 최신순 책방조회 콜백 함수
    fun onGetNewestSuccess(response: GetNewestResponse)

    fun onGetNewestFailure(message: String)

    // 인기순 책방조회 콜백 함수
    fun onGetPopularSuccess(response: GetPopularResponse)

    fun onGetPopularFailure(message: String)

    // 책방 검색 콜백 함수
    fun onGetSearchSuccess(response: GetSearchResponse, query: String?)

    fun onGetSearchFailure(message: String)
}