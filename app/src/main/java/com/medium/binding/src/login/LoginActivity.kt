package com.medium.binding.src.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseActivity
import com.medium.binding.databinding.ActivityLoginBinding
import com.medium.binding.src.find_password.FindPasswordActivity
import com.medium.binding.src.join.JoinActivity
import com.medium.binding.src.login.models.LoginResponse
import com.medium.binding.src.main.MainActivity

class LoginActivity: BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate),
LoginActivityView{

    private val sp = ApplicationClass.sSharedPreferences

    private lateinit var email: String
    private lateinit var pwd: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 에러문구를 감춘다
       binding.loginWrongInfo.visibility = View.INVISIBLE

        // 회원가입 버튼 클릭
        binding.loginJoin.setOnClickListener(onClickJoin)

        // 비밀번호 찾기 버튼
        binding.loginFindPw.setOnClickListener(onClickPassword)

        // 로그인(다음) 버튼 클릭
        binding.loginNext.setOnClickListener(onClickNext)

        // 뒤로가기 버튼 클릭 -> 종료
        binding.loginBack.setOnClickListener{
            finish()
        }

        // 각 입력칸이 포커싱되면 밑줄이 굵어진다
        binding.loginEmail.onFocusChangeListener = onFocusEmail
        binding.loginPassword.onFocusChangeListener = onFocusPwd

        // 첫번째 text칸 엔터키 -> 아래 editText로 이동
        binding.loginEmail.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                binding.loginPassword.performClick()
            }
            false
        }

        // 2번째 editText 엔터버튼 클릭 -> 다음 버튼 자동 눌림
        binding.loginPassword.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENDCALL || keyCode == KeyEvent.KEYCODE_ENTER)
            ){
                binding.loginNext.performClick()
            }
            false
        }
    }

    private val onClickPassword = View.OnClickListener {
        val passwordIntent = Intent(this, FindPasswordActivity::class.java)
        startActivity(passwordIntent)
        finish()
    }

    // 로그인(다음) 버튼 클릭
    private val onClickNext = View.OnClickListener {
        email = binding.loginEmail.text.toString()
        pwd = binding.loginPassword.text.toString()

        // 로그인 API 호출
        showLoadingDialog(this)
        LoginService(this).tryPostLogin(email, pwd)
    }

    // 회원가입을 클릭하면 JoinActivity(회원가입 첫번째 프래그먼트)로 이동한다
    private val onClickJoin = View.OnClickListener{
        val joinIntent = Intent(this, JoinActivity::class.java)
        startActivity(joinIntent)
        finish()
    }

    // 이메일 입력 칸 포커스 -> 밑줄이 굵어진다
    private val onFocusEmail = View.OnFocusChangeListener { v, hasFocus ->
        when(hasFocus ){
            true -> binding.loginEmailLineBold.visibility = View.VISIBLE
            false -> binding.loginEmailLineBold.visibility = View.INVISIBLE
        }
    }

    // 비밀번호 입력 칸 포커스 -> 밑줄이 굵어진다
    private val onFocusPwd = View.OnFocusChangeListener { v, hasFocus ->
        when(hasFocus){
            true -> binding.loginPwLineBold.visibility = View.VISIBLE
            false -> binding.loginPwLineBold.visibility = View.INVISIBLE
        }
    }

    // 로그인 API 통신 성공
    override fun onPostLoginSuccess(response: LoginResponse) {
        dismissLoadingDialog()

        when(response.code){

            // 로그인 성공 -> jwt 저장하고 메인 화면으로 이동
            1000 -> {
                // jwt, 패스워드 저장
                sp.edit().apply {
                    this.putString(ApplicationClass.X_ACCESS_TOKEN, response.jwt)
                    this.putString("pw", pwd)
                    ApplicationClass.userIdx = response.userIdx
                    this.putInt("userIdx", response.userIdx)
                    this.apply()
                }

                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            }

            // 로그인 실패 -> 에러 문구
            else -> {
                binding.loginWrongInfo.visibility = View.VISIBLE
            }
        }
    }

    // 로그인 API 통신 실패
    override fun onPostLoginFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("로그인 중 에러가 발생했습니다\n네트워크 확인 후 에러가 계속되면 관리자에게 문의해주세요.")
    }
}