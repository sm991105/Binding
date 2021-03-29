package com.medium.binding.src.find_password

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.medium.binding.config.BaseActivity
import com.medium.binding.config.BaseResponse
import com.medium.binding.databinding.ActivityFindPasswordBinding
import com.medium.binding.src.login.LoginActivity
import java.util.regex.Pattern


class FindPasswordActivity:
    BaseActivity<ActivityFindPasswordBinding>(ActivityFindPasswordBinding::inflate),
    FindPasswordActivityView
{

    private var isHere = true

    // 좀 더 디테일한 패턴 - 이메일 정규식
    private val emailExp = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:" +
            "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\" +
            "x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9]" +
            "(?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
            "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b" +
            "\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
    // 닉네임 - 올바른 자음모음만 가능
    private val nicknameExp = "^(?=.*[가-힣]).{2,8}$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isHere = true

        // 각 입력칸이 포커싱되면 밑줄이 굵어진다
        binding.findPwNickname.onFocusChangeListener = onFocusEmail
        binding.findPwEmail.onFocusChangeListener = onFocusPwd

        binding.findPwNext.setOnClickListener { onClickNext }

        // 첫번째 text칸 엔터키 -> 아래 editText로 이동
        binding.findPwNickname.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER ||
                    keyCode == KeyEvent.KEYCODE_ENDCALL)){
                binding.findPwEmail.performClick()
                true
            }
            false
        }

        // 2번째 editText 엔터버튼 클릭 -> 다음 버튼 자동 눌림
        binding.findPwEmail.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENDCALL || keyCode == KeyEvent.KEYCODE_ENTER)
            ){
                binding.findPwNext.performClick()
                true
            }
            false
        }

        // 뒤로가기 버튼
        binding.findPwBack.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            isHere = false
            startActivity(loginIntent)
            finish()
        }

        binding.findPwNext.setOnClickListener(onClickNext)
    }

    // 이메일 입력 칸 포커스 -> 밑줄이 굵어진다
    private val onFocusEmail = View.OnFocusChangeListener { v, hasFocus ->
        when(hasFocus ){
            true -> binding.findPwNicknameLineBold.visibility = View.VISIBLE
            false ->
                if (isHere) {
                    binding.findPwNicknameLineBold.visibility = View.INVISIBLE
                }
        }
    }

    // 비밀번호 입력 칸 포커스 -> 밑줄이 굵어진다
    private val onFocusPwd = View.OnFocusChangeListener { v, hasFocus ->
        when(hasFocus){
            true -> {
                binding.findPwEmailLineBold.visibility = View.VISIBLE
                val manager: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
            }
            false ->
                if (isHere) {
                    binding.findPwEmailLineBold.visibility = View.INVISIBLE
                }
        }
    }

    private val onClickNext = View.OnClickListener {
        val email = binding.findPwEmail.text.toString()
        val nickname = binding.findPwNickname.text.toString()

        // 닉네임 형식이 올바르지 않으면 에러 메시지를 보여준다
        if(!isValidNickname(nickname)){
            Log.d("로그", "nickname: $nickname , isValid: ${isValidNickname(nickname)}")

            binding.findPwWrongInfo.let{
                it.text = String.format("유효하지 않은 닉네임 형식입니다.")
                it.visibility = View.VISIBLE
            }
        }
        // 이메일 형식이 올바르지 않을 때
        else if(!isValidEmail(email)){
            Log.d("로그", "nickname: $email , isValid: ${isValidEmail(email)}")

            binding.findPwWrongInfo.let{
                it.text = String.format("유효하지 않은 이메일 형식입니다.")
                it.visibility = View.VISIBLE
            }
        }
        // 형식이 올바르면 비밀번호 찾기 API 호출 -> 임시 비밀번호 발급 요청
        else{
            Log.d("로그", "비밀번호 찾기 api 호출")

            showLoadingDialog(this)
            FindPasswordService(this).tryPostFindPw(email, nickname)
        }

    }

    override fun onPostFindPwSuccess(response: BaseResponse) {
        Log.d("로그", "onPostFindPwSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){

            // 성공 -> 임시비밀번호 발급
            1000 -> {
                Log.d("로그", "회원가입 성공 - message: ${response.message}")

                isHere = false
                // 임시비밀번호 보냄 완료 화면을 띄운다
                val mTempPwDialog = TempPasswordDialog(this)
                val fragmentManager = supportFragmentManager
                mTempPwDialog.show(fragmentManager, "temp_pw")
            }
            3001 -> {
                binding.findPwWrongInfo.let {
                    it.text = String.format("등록되지 않은 이메일입니다.")
                    it.visibility = View.VISIBLE
                }
            }
            3002 -> {
                binding.findPwWrongInfo.let {
                    it.text = String.format("닉네임이 일치하지 않습니다.")
                    it.visibility = View.VISIBLE
                }
            }
            else -> {
                binding.findPwWrongInfo.let{
                    it.text = response.message
                    it.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onPostFindPwFailure(message: String) {
        Log.d("로그", "onPostFindPwFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }

    // 정규식으로 한글 닉네임 형식 검사
    private fun isValidNickname(nickname: String):Boolean {
        var returnValue = false

        try
        {
            val p = Pattern.compile(nicknameExp)
            val m = p.matcher(nickname)

            if (m.matches()) {
                returnValue = true
            }

            return returnValue
        }
        catch (e: Exception) {
            return false
        }
    }

    // 정규식으로 이메일 형식 검사하는 함수
    fun isValidEmail(emailAddress: String):Boolean {
        var returnValue = false

        try
        {
            val p = Pattern.compile(emailExp)
            val m = p.matcher(emailAddress)

            if (m.matches() && emailAddress.length < 30) {
                returnValue = true
            }

            return returnValue
        }
        catch (e: Exception) {
            return false
        }
    }

    fun moveToLogin(){
        val loginIntent = Intent(this, LoginActivity::class.java)
        isHere = false
        startActivity(loginIntent)
        finish()
    }

    override fun onBackPressed() {
        moveToLogin()
    }
}