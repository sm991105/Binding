package com.medium.binding.src.main.my_page.settings.profile

import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseResponse
import com.medium.binding.src.main.my_page.settings.models.GetProfileResponse
import com.medium.binding.src.main.my_page.settings.models.PatchProfileBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsProfileService(val view: SettingsProfileActivityView) {

    // 유저 프로필 조회 함수(네트워크 통신)
    fun tryGetProfile(){

        val settingsProfileRetrofitInterface = ApplicationClass.sRetrofit.create(
            SettingsProfileRetrofitInterface::class.java)

        settingsProfileRetrofitInterface.getProfile()
            .enqueue(object : Callback<GetProfileResponse> {

                override fun onResponse(call: Call<GetProfileResponse>,
                                        response: Response<GetProfileResponse>
                ) {
                    view.onGetProfileSuccess(response.body() as GetProfileResponse)
                }

                override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                    view.onGetProfileFailure(t.message ?: "통신 오류")
                }
            })
    }


    // 유저 프로필 변경( 네트워크 통신)
    fun tryPatchProfile(profileBody: PatchProfileBody){

        val settingsProfileRetrofitInterface = ApplicationClass.sRetrofit.create(
            SettingsProfileRetrofitInterface::class.java)

        settingsProfileRetrofitInterface.patchProfile(profileBody)
            .enqueue(object : Callback<BaseResponse> {

                override fun onResponse(call: Call<BaseResponse>,
                                        response: Response<BaseResponse>
                ) {
                    view.onPatchProfileSuccess(response.body() as BaseResponse)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onPatchProfileFailure(t.message ?: "통신 오류")
                }
            })
    }
}