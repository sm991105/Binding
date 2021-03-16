package com.example.binding.src.join.join1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.example.binding.R
import com.example.binding.config.BaseFragment
import com.example.binding.config.BaseResponse
import com.example.binding.databinding.FragmentJoin1Binding
import com.example.binding.src.join.JoinActivity
import java.util.regex.Pattern

class Join1Fragment: BaseFragment<FragmentJoin1Binding>(
    FragmentJoin1Binding::bind,
    R.layout.fragment_join_1
), Join1FragmentView {
    // 이메일 정규식
    // 이메일 host 부에 ..의 중복을 막지 못한다
    // val regExp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,6}$"

    // 좀 더 디테일한 패턴
    private val regExp = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:" +
            "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\" +
            "x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9]" +
            "(?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
            "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b" +
            "\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 에러 메시지, 굵은 선을 감춘다
        binding.join1BadEmail.visibility = View.INVISIBLE
        binding.join1SameEmail.visibility = View.INVISIBLE
        binding.join1LineBold.visibility = View.INVISIBLE

        // 다음 버튼 클릭
        binding.join1Next.setOnClickListener(onClickNext)

        // 뒤로가기 버튼 -> 로그인 화면으로 돌아간다
        binding.join1Back.setOnClickListener{
            (activity!! as JoinActivity).moveToLogin(false)
        }

        // 엔터 -> next 버튼 클릭
        binding.join1Email.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENDCALL || keyCode == KeyEvent.KEYCODE_ENTER)
            ){
                binding.join1Next.performClick()
                true
            }
            false
        }

        binding.join1Email.onFocusChangeListener = onFocusChange

        /*// 입력하는 동안 이메일 형식 검사
        binding.join1Email.addTextChangedListener(emailWatcher)*/



    }

    // 입력 칸이 포커싱되면 아래에 굵은 선을 보여준다
    private val onFocusChange = View.OnFocusChangeListener { v, hasFocus ->
        when(hasFocus){
            true -> binding.join1LineBold.visibility = View.VISIBLE
        }
    }

    // 다음 버튼을 클릭하면 이메일 중복 검사 API를 호출한다
    private val onClickNext = View.OnClickListener{
        when(isValidEmail(binding.join1Email.text.toString())){

            // 형식이 올바르면 중복확인 API 호출
            true -> Join1Service(this).tryGetCheckEmail(binding.join1Email.text.toString())

            // 형식이 틀리면 에러 문구 출력
            false -> {
                binding.join1SameEmail.visibility = View.INVISIBLE
                binding.join1BadEmail.visibility = View.VISIBLE
            }
        }
    }

    // 정규식으로 이메일 형식 검사하는 함수
    fun isValidEmail(emailAddress: String):Boolean {
        var returnValue = false

        try
        {
            val p = Pattern.compile(regExp)
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

    // 입력하는 중에 이메일 형식 검사
    private val emailWatcher = object: TextWatcher{

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when(isValidEmail(s.toString())){
                true -> binding.join1BadEmail.visibility = View.INVISIBLE
                false -> {
                    binding.join1BadEmail.visibility = View.VISIBLE
                    binding.join1SameEmail.visibility = View.INVISIBLE
                }
            }
        }
    }
    // 서버와 통신 성공
    override fun onGetCheckEmailSuccess(response: BaseResponse, email: String) {
        Log.d("로그", "onGetCheckEmailFailure() called, response: $response")

        when(response.code){

            // 사용 가능한 이메일
            1000 -> {
                Log.d("로그", "사용가능한 이메일, message: ${response.message}")

                // 다음단계 -> 비밀번호 입력 화면으로 이동
                (activity!! as JoinActivity).moveToJoin2(email)
            }

            // 중복 이메일 -> 중복 이메일 문구를 보여준다
            3000 -> {
                binding.join1BadEmail.visibility = View.INVISIBLE
                binding.join1SameEmail.visibility = View.VISIBLE
            }

            // 서버에서 이메일 형식 오류로 판정 -> 에러 문구를 보여준다
            else ->{
                Log.d("로그", "code: ${response.code}, msg: ${response.message}")

                binding.join1BadEmail.visibility = View.VISIBLE
                binding.join1SameEmail.visibility = View.INVISIBLE
            }
        }
    }

    // 서버 통신 실패
    override fun onGetCheckEmailFailure(message: String) {
        Log.d("로그", "onGetCheckEmailFailure() called, message: $message")

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }
}