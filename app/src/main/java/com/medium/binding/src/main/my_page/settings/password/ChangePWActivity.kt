package com.medium.binding.src.main.my_page.settings.password

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseActivity
import com.medium.binding.config.BaseResponse
import com.medium.binding.databinding.ActivitySettingsChangePwBinding
import com.medium.binding.src.main.my_page.settings.models.PatchPWBody

class ChangePWActivity : BaseActivity<ActivitySettingsChangePwBinding>(
    ActivitySettingsChangePwBinding::inflate
), ChangePWActivityView {

    private val sp = ApplicationClass.sSharedPreferences
    private var currentPW: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 첫번째 text칸 엔터키 -> 아래 editText로 이동
        binding.settingsChangePwOld.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                binding.settingsChangePwNew.requestFocus()
            }
            false
        }

        // 두번째 text칸 엔터키 -> 아래 editText로 이동
        binding.settingsChangePwNew.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                binding.settingsChangePwNew2.requestFocus()
            }
            false
        }

        // 세번째 editText 엔터버튼 클릭 -> 완료 버튼 자동 클릭
        binding.settingsChangePwNew.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENDCALL || keyCode == KeyEvent.KEYCODE_ENTER)
            ){
                binding.settingsChangePwDone.performClick()
            }
            false
        }

        // 현재 비밀번호
        currentPW = sp.getString("pw", null)

        // 뒤로가기 버튼
        binding.settingsChangePwLeft.setOnClickListener {
            finish()
        }

        // 완료 버튼
        binding.settingsChangePwDone.setOnClickListener(onClickDone)
    }

    // 완료 버튼 클릭
    private val onClickDone = View.OnClickListener {
        val currentPwStr = binding.settingsChangePwOld.text.toString()
        val newPwStr = binding.settingsChangePwNew.text.toString()
        val newPwChkStr = binding.settingsChangePwNew2.text.toString()

        /*// 비밀번호 형식이 올바르지 않을 때
        if(!isValidPwd(pwdString)){
            Log.d("로그", "비밀번호 올바르지 않음")
            binding.join2WrongPw.let {
                it.text = String.format("비밀번호 형식이 맞지 않습니다")
                it.visibility = View.VISIBLE
            }
        }*/

        // 입력한 현재 비밀번호가 8-20자가 아닐때
        if (currentPwStr.length < 8 || currentPwStr.length > 20) {
            binding.settingsChangePwWrong.text = String.format("현재 비밀번호를 8~20자로 입력해주세요")
        }
        // 현재 비밀번호가 일치하지 않을 때
        else if(currentPwStr != currentPW){
            binding.settingsChangePwWrong.text = String.format("현재 비밀번호가 일치하지 않습니다")
        }
        // 새로운 비밀번호가 8-20자가 아닐 때
        else if (newPwStr.length < 8 || newPwStr.length > 20) {
            binding.settingsChangePwWrong.text =
                String.format("새로운 비밀번호를 8~20자로 입력해주세요")
        }
        // 새 비밀번호와 새 비밀번호 확인이 일치하지 않을 때
        else if (newPwStr != newPwChkStr) {
            binding.settingsChangePwWrong.text = String.format("새로운 비밀번호가 일치하지 않습니다")
        }
        // 형식이 올바르고, 새 비밀번호가 일치하면 변경 API를 호출한다
        else {
            val patchPWBody = PatchPWBody(currentPwStr, newPwStr, newPwChkStr)
            // API 호출
            showLoadingDialog(this)
            ChangePWService(this).tryPatchPW(patchPWBody)
        }
    }

    // 비밀번호 변경 네트워크 통신 성공
    override fun onPatchPWSuccess(response: BaseResponse) {
        dismissLoadingDialog()

        when(response.code){

            // 성공
            1000 -> {
                // 바뀐 비밀번호 저장
                sp.edit().putString("pw", binding.settingsChangePwNew.text.toString()).apply()
                finish()
            }

            // 새로운 비밀번호 불일치
            2001 -> {
                binding.settingsChangePwWrong.text =
                    String.format("새로운 비밀번호가 일치하지 않습니다")
            }

            // 새비밀번호 8-20자 불일치
            2002 -> binding.settingsChangePwWrong.text =
                String.format("새로운 비밀번호를 8~20자로 입력해주세요")

            // 변경 전 후 비밀번호가 같다
            2003 -> binding.settingsChangePwWrong.text =
                String.format("현재 비밀번호와 다른 비밀번호를 사용해주세요")

            // 현재 비밀번호 불일치
            3001 -> binding.settingsChangePwWrong.text =
                String.format("현재 비밀번호가 일치하지 않습니다")
        }
    }

    // 비밀번호 변경 네트워크 통신 실패
    override fun onPatchPWFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("비밀번호 변경 시도 중 오류가 발생했습니다\n" +
                "네트워크 확인 후 오류가 계속되면 관리자에게 문의해주세요")
    }
}