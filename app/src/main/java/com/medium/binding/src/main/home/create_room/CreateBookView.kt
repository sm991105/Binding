package com.medium.binding.src.main.home.create_room

import com.medium.binding.src.main.home.create_room.models.CreateBookResponse

interface CreateBookView {

    // 최신순 책방조회 콜백 함수
    fun onPostBookSuccess(response: CreateBookResponse)

    fun onPostBookFailure(message: String)
}