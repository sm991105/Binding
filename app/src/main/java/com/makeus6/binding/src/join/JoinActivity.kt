package com.makeus6.binding.src.join

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.makeus6.binding.R
import com.makeus6.binding.config.BaseActivity
import com.makeus6.binding.databinding.ActivityJoinBinding
import com.makeus6.binding.src.join.join1.Join1Fragment
import com.makeus6.binding.src.join.join2.Join2Fragment
import com.makeus6.binding.src.join.join3.Join3Fragment
import com.makeus6.binding.src.login.LoginActivity


class JoinActivity: BaseActivity<ActivityJoinBinding>(ActivityJoinBinding::inflate){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // join_frm 프레임레이아웃에 Join1 프래그먼트를 올린다
        supportFragmentManager.beginTransaction()
            .add(R.id.join_frm, Join1Fragment())
            .addToBackStack("join1")
            .commitAllowingStateLoss()
    }

    // 회원가입 2번째 화면, 비밀번호 입력창으로 이동
    fun moveToJoin2(email: String) {

        // 입력받은 이메일 주소를 Join2Fragment로 전달
        val bundle = Bundle()
        bundle.putString("email", email)

        val join2Fragment = Join2Fragment()
        join2Fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
                .addToBackStack("join2")
                .replace(R.id.join_frm, join2Fragment)
                .commitAllowingStateLoss()
    }

    // 회원가입 마지막 화면, 닉네임 입력창으로 이동
    fun moveToJoin3(email:String, pwd: String, pwdCheck: String) {

        // join1,2에서 입력받은 이메일, 비밀번호, 비밀번호 확인을 join3Fragment로 전달
        val bundle = Bundle()
        bundle.putString("email", email)
        bundle.putString("pwd", pwd)
        bundle.putString("pwdChk", pwdCheck)

        val join3Fragment = Join3Fragment()
        join3Fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .addToBackStack("join3")
            .replace(R.id.join_frm, join3Fragment)
            .commitAllowingStateLoss()
    }

    // 로그인 화면으로 이동
    fun moveToLogin(isJoined: Boolean){
        val loginIntent = Intent(this, LoginActivity::class.java)
        // 회원가입 성공 후 로그인 화면으로 돌아오는건지 확인
        loginIntent.putExtra("isJoined", isJoined)
        startActivity(loginIntent)
        finish()
    }

    // 회원가입 중 백 버튼 -> 이전 프래그먼트로 돌아간다
    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager

        if(fragmentManager.backStackEntryCount > 0){
            fragmentManager.popBackStack()
            if(fragmentManager.backStackEntryCount == 0){
                super.onBackPressed()
            }
        }else{
            super.onBackPressed()
        }
    }
}