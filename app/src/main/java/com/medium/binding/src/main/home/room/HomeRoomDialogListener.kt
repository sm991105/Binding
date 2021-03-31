package com.medium.binding.src.main.home.room

interface HomeRoomDialogListener {
    fun onClickReport(reportReason: String, contentIdx: Int)

    fun onClickRemove(contentIdx: Int)
}