package com.makeus6.binding.src.main.my_page.settings

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.makeus6.binding.R
import com.makeus6.binding.config.ApplicationClass
import com.makeus6.binding.config.BaseFragment
import com.makeus6.binding.databinding.FragmentSettingsBinding
import com.makeus6.binding.src.login.LoginActivity
import com.makeus6.binding.src.main.my_page.MyPageFragmentView
import com.makeus6.binding.src.main.my_page.settings.logout.LogoutDialog
import com.makeus6.binding.src.main.my_page.settings.password.ChangePWActivity
import com.makeus6.binding.src.main.my_page.settings.profile.SettingsProfileActivity


class SettingsFragment(private val myPageFragment: MyPageFragmentView): BaseFragment<FragmentSettingsBinding>(
    FragmentSettingsBinding::bind,
    R.layout.fragment_settings
){
    private val sp = ApplicationClass.sSharedPreferences
    lateinit var logoutDialog: LogoutDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그아웃 클릭시 나오는 다이얼로그
        logoutDialog = LogoutDialog(context!!, confirmLogout, cancelLogout)

        // 프로필 관리 버튼
        binding.settingsProfile.setOnClickListener(onClickSettings)

        // 비밀번호 변경 버튼
        binding.settingsChangePw.setOnClickListener(onClickChangePW)

        // 로그아웃 버튼
        binding.settingsLogout.setOnClickListener{
            logOut()
        }
    }

    // 프로필 관리 버튼 리스너
    private val onClickSettings = View.OnClickListener {
        val profileIntent = Intent(context, SettingsProfileActivity::class.java)
        startActivity(profileIntent)
    }
    // 비밀번호 변경 버튼 리스너
    private val onClickChangePW = View.OnClickListener {
        val pwIntent = Intent(context, ChangePWActivity::class.java)
        startActivity(pwIntent)
    }

    // 로그아웃 함수
    private fun logOut(){
        logoutDialog.show()

        /*sp.edit().apply{
            this.putString(ApplicationClass.X_ACCESS_TOKEN, null)
            this.putString("pw", null).apply()
        }

        // 로그인 화면으로 이동
        val loginIntent = Intent(context!!, LoginActivity::class.java)
        startActivity(loginIntent)
        activity!!.finish()*/
    }

    private val confirmLogout = View.OnClickListener {
        sp.edit().apply{
            this.putString(ApplicationClass.X_ACCESS_TOKEN, null)
            this.putString("pw", null).apply()
        }

        // 로그인 화면으로 이동
        val loginIntent = Intent(context!!, LoginActivity::class.java)
        startActivity(loginIntent)
        activity!!.finish()
    }

    private val cancelLogout = View.OnClickListener{
        logoutDialog.dismiss()
    }
}