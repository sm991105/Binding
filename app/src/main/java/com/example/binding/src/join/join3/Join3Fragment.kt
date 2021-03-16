package com.example.binding.src.join.join3

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.binding.R
import com.example.binding.config.BaseFragment
import com.example.binding.config.BaseResponse
import com.example.binding.databinding.FragmentJoin3Binding
import com.example.binding.src.join.JoinActivity
import com.example.binding.src.join.join3.models.PostJoinBody
import com.example.binding.src.join.done.JoinDoneDialog
import java.util.regex.Pattern

class Join3Fragment: BaseFragment<FragmentJoin3Binding>(
    FragmentJoin3Binding::bind,
    R.layout.fragment_join_3
), Join3FragmentView {

    // 한글(자음+모음+공백+언더바+숫자)만 허용하는 정규표현식, 2~8자
    // private val regExp =  "^(?=.*[ㄱ-ㅎ|ㅏ-ㅣ|가-힣\\s_0-9]).{2,8}$"

    // 한글(자음+모음)만 허용하는 식, 2~8자
    // private val regExp = "^(?=.*[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]).{2,8}$"
    private val regExp = "^(?=.*[가-힣]).{2,8}$"
    // 입력받은 닉네임 저장 변수
    lateinit var nickname: String
    // 넘겨받은 이메일 주소, 비밀번호, 비밀번호 확인 임시 값
    /*private var email = "temp_ghdtkdgh5@naver.com"
    private var pwd = "12345678a"
    private var pwdChk = "12345678a"*/

    private var isHere = true

    // 넘겨받은 이메일 주소, 비밀번호, 비밀번호 확인
    lateinit var email: String
    lateinit var pwd: String
    lateinit var pwdChk: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isHere = true

        // 넘겨받은 이메일 주소, 비밀번호, 비밀번호 확인
        email = arguments?.getString("email","temp_ghdtkdgh5@naver.com").toString()
        pwd = arguments?.getString("pwd","12345678a").toString()
        pwdChk = arguments?.getString("pwdChk","12345678a").toString()


        // 다음 버튼 클릭 -> 닉네임 형식이 올바르면 중복 닉네임 api 호출
        binding.join3Next.setOnClickListener(onClickNext)

        // 에러 메세지들을 감춘다.
        binding.join3WrongNickname.visibility = View.INVISIBLE
        binding.join3SameNickname.visibility = View.INVISIBLE

        // 뒤로가기 버튼 -> 로그인 화면으로 돌아간다
        binding.join3Back.setOnClickListener{
            (activity!! as JoinActivity).moveToLogin(false)
        }
    }

    private val onClickNext = View.OnClickListener {

        // 입력 받은 닉네임
        nickname = binding.join3Name.text.toString()

        // 닉네임 형식이 올바르지 않으면 에러 메시지를 보여준다
        if(!isValidNickname(nickname)){
            Log.d("로그","nickname: $nickname , isValid: ${isValidNickname(nickname)}")

            binding.join3SameNickname.visibility = View.INVISIBLE
            binding.join3WrongNickname.visibility = View.VISIBLE
        }
        // 형식이 올바르면 중복 닉네임 검사 + 회원가입 api 호출
        else{
            val joinBody = PostJoinBody(email, pwd, pwdChk, nickname)
            Log.d("로그","회원가입 api 호출 - postJoinBody: $joinBody")
            Join3Service(this).tryPostJoin(joinBody)
        }
    }

    // 정규식으로 한글 닉네임 형식 검사
    private fun isValidNickname(nickname: String):Boolean {
        var returnValue = false

        try
        {
            val p = Pattern.compile(regExp)
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

    // 회원가입 api 통신 성공
    override fun onPostJoinSuccess(response: BaseResponse) {
        Log.d("로그", "onPostJoinSuccess() called, response: $response")

        when(response.code){

            // 회원가입에 성공 -> 로그인 액티비티로 돌아간다
            1000 -> {
                Log.d("로그", "회원가입 성공 - message: ${response.message}")

                isHere = false
                // 회원가입 완료 화면을 띄운다
                val mJoinDialog = JoinDoneDialog(activity as Context, response.isSuccess)
                val fragmentManager = childFragmentManager
                mJoinDialog.show(fragmentManager, "join_done")
            }

            // 닉네임 형식 오류
            in 2007..2009 -> {
                Log.d("로그", "회원가입 실패 - message: ${response.message}," +
                        " code: ${response.code}")

                binding.join3WrongNickname.visibility = View.VISIBLE
                binding.join3SameNickname.visibility = View.INVISIBLE
            }

            // 이미 사용중인 닉네임
            3001 -> {
                Log.d("로그", "회원가입 실패 - message: ${response.message}," +
                        " code: ${response.code}")

                binding.join3WrongNickname.visibility = View.INVISIBLE
                binding.join3SameNickname.visibility = View.VISIBLE
            }

            // 회원가입 실패
            else -> {
                Log.d("로그", "회원가입 실패 - message: ${response.message}," +
                        " code: ${response.code}")

                binding.join3SameNickname.visibility = View.INVISIBLE
            }
        }
    }

    // 회원가입 api 통신 실패
    override fun onPostJoinFailure(message: String) {
        Log.d("로그", "onPostJoinFailure() called, message: $message")

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }
}