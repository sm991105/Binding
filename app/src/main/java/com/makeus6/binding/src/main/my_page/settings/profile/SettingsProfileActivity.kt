package com.makeus6.binding.src.main.my_page.settings.profile

import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.makeus6.binding.R
import com.makeus6.binding.config.BaseActivity
import com.makeus6.binding.databinding.ActivitySettingsProfileBinding
import com.makeus6.binding.src.main.my_page.settings.models.GetProfileResponse

class SettingsProfileActivity : BaseActivity<ActivitySettingsProfileBinding>(
    ActivitySettingsProfileBinding::inflate
), SettingsProfileActivityView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 프로필 정보를 불러온다
        showLoadingDialog(this)
        SettingsProfileService(this).tryGetProfile()

        binding.settingsProfileLeft.setOnClickListener {
            finish()
        }
    }

    override fun onGetProfileSuccess(response: GetProfileResponse) {
        Log.d("로그", "onGetProfileFailure() called, response: $response")
        dismissLoadingDialog()

        when(response.code){

            // 프로필 조회 성공 -> 프로필 정보를 보여준다
            1000 -> {
                if(response.result.size > 0){
                    val result = response.result[0]
                    binding.settingsProfileNickname.setText(result.nickname)
                    binding.settingsProfileEmail.setText(result.email)

                    Glide.with(this)
                        .load(result.userImgUrl)
                        .error(R.drawable.icon_app)
                        .placeholder(R.drawable.icon_app)
                        .into(binding.settingsProfilePhoto)
                }
            }
            else -> {
                Log.d("로그", "프로필 조회 실패, code: ${response.code}, message:" +
                        " ${response.message}"
                )
            }
        }
    }

    override fun onGetProfileFailure(message: String) {
        Log.d("로그", "onGetProfileFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }
}