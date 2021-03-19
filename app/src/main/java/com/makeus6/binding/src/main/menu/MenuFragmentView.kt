package com.makeus6.binding.src.main.menu

import com.makeus6.binding.src.main.menu.models.GetStoresResponse

interface MenuFragmentView {

    // 전체 서점 API 콜백 함수
    fun onGetAllStoresSuccess(response: GetStoresResponse)

    fun onGetAllStoresFailure(message: String)

    // 지역 서점 API 콜백 함수
    fun onGetLocationStoresSuccess(response: GetStoresResponse)

    fun onGetLocationStoresFailure(message: String)

    // 바텀시트에서 지역 선택을 완료했을 때 실행할 콜백함수
    fun updateLocationStores(LocationList: ArrayList<String>)

    // 전체 지역 선택
    fun getAllStores()

    // 지역 이름 변경
    fun updateLocationTxt(location: String)

    // 지역 pos 변경
    fun updateLocationPos(bigPosition: Int, smallPosition: Int)
}