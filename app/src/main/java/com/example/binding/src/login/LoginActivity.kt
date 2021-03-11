package com.example.binding.src.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.binding.R
import com.example.binding.config.ApplicationClass
import com.example.binding.config.BaseActivity
import com.example.binding.databinding.ActivityLoginBinding
import com.example.binding.src.join.JoinActivity
import com.example.binding.src.login.models.LoginResponse
import com.example.binding.src.main.MainActivity
import com.example.binding.util.JoinDialog

class LoginActivity: BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate),
LoginActivityView{

    // 다른 화면으로 넘어가는 과정에서 binding이 풀려서 생기는 에러를 막기 위한 플래그
    private var isHere = true

    private val sp = ApplicationClass.sSharedPreferences

    private lateinit var email: String
    private lateinit var pwd: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isHere = true

        // 에러문구를 감춘다
       binding.loginWrongInfo.visibility = View.INVISIBLE

        // 회원가입 버튼 클릭
        binding.loginJoin.setOnClickListener(onClickJoin)

        // 로그인(다음) 버튼 클릭
        binding.loginNext.setOnClickListener(onClickNext)

        // 뒤로가기 버튼 클릭 -> 종료
        binding.loginBack.setOnClickListener{
            finish()
        }

        // 각 입력칸이 포커싱되면 밑줄이 굵어진다
        binding.loginEmail.onFocusChangeListener = onFocusEmail
        binding.loginPassword.onFocusChangeListener = onFocusPwd
    }

    // 로그인(다음) 버튼 클릭
    private val onClickNext = View.OnClickListener {
        email = binding.loginEmail.text.toString()
        pwd = binding.loginPassword.text.toString()

        // 로그인 API 호출
        LoginService(this).tryPostLogin(email, pwd)
        // JoinDialog(this, true).show()
    }

    // 회원가입을 클릭하면 JoinActivity(회원가입 첫번째 프래그먼트)로 이동한다
    private val onClickJoin = View.OnClickListener{
        val joinIntent = Intent(this, JoinActivity::class.java)
        isHere = false
        startActivity(joinIntent)
        finish()
    }

    // 이메일 입력 칸 포커스 -> 밑줄이 굵어진다
    private val onFocusEmail = View.OnFocusChangeListener { v, hasFocus ->
        when(hasFocus ){
            true -> binding.loginEmailLineBold.visibility = View.VISIBLE
            false ->
                if(isHere){
                    binding.loginEmailLineBold.visibility = View.INVISIBLE
                }
        }
    }

    // 비밀번호 입력 칸 포커스 -> 밑줄이 굵어진다
    private val onFocusPwd = View.OnFocusChangeListener { v, hasFocus ->
        when(hasFocus){
            true -> binding.loginPwLineBold.visibility = View.VISIBLE
            false ->
                if(isHere){
                    binding.loginPwLineBold.visibility = View.INVISIBLE
                }
        }
    }

    // 로그인 API 통신 성공
    override fun onPostLoginSuccess(response: LoginResponse) {
        Log.d("로그", "onPostLoginSuccess() called, response: $response")

        when(response.code){

            // 로그인 성공 -> jwt 저장하고 메인 화면으로 이동
            1000 -> {
                Log.d("로그", "로그인 성공, jwt: ${response.jwt}")

                // jwt 저장
                sp.edit().putString(ApplicationClass.X_ACCESS_TOKEN, response.jwt).apply()

                val mainIntent = Intent(this, MainActivity::class.java)
                isHere = false
                startActivity(mainIntent)
                finish()
            }

            // 로그인 실패 -> 에러 문구
            else -> {
                Log.d("로그","로그인 실패, msg: ${response.message}")

                binding.loginWrongInfo.visibility = View.VISIBLE
            }
        }
    }

    // 로그인 API 통신 실패
    override fun onPostLoginFailure(message: String) {
        Log.d("로그", "onPostLoginFailure() called, message: $message")

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }
}