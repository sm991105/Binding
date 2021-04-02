package com.medium.binding.src.main.my_page.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseFragment
import com.medium.binding.config.BaseResponse
import com.medium.binding.databinding.FragmentSettingsBinding
import com.medium.binding.src.login.LoginActivity
import com.medium.binding.src.main.my_page.MyPageFragmentView
import com.medium.binding.src.main.my_page.settings.logout.LogoutDialog
import com.medium.binding.src.main.my_page.settings.withdraw.WithdrawDialog
import com.medium.binding.src.main.my_page.settings.password.ChangePWActivity
import com.medium.binding.src.main.my_page.settings.profile.SettingsProfileActivity
import com.medium.binding.src.main.my_page.settings.report.SettingsReportActivity


class SettingsFragment(private val myPageFragment: MyPageFragmentView): BaseFragment<FragmentSettingsBinding>(
    FragmentSettingsBinding::bind,
    R.layout.fragment_settings
), SettingsFragmentView{
    private val sp = ApplicationClass.sSharedPreferences
    private lateinit var logoutDialog: LogoutDialog
    private lateinit var withdrawDialog: WithdrawDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ChildFragment(SettingsFragment) 하단 뷰로 터치 이벤트가 전달되는 것을 막는다
        binding.settingsRoot.setOnTouchListener { v, event ->
            v.performClick()
            true
        }

        binding.settingsLeft.setOnClickListener {
            activity?.onBackPressed()
        }

        // 로그아웃 클릭시 나오는 다이얼로그
        logoutDialog = LogoutDialog(context!!, confirmLogout, cancelLogout)

        // 회원탈퇴 클릭시 나오는 다이얼로그
        withdrawDialog = WithdrawDialog(context!!, confirmWithdraw, cancelWithdraw)

        // 프로필 관리 버튼
        binding.settingsProfile.setOnClickListener(onClickSettings)

        // 비밀번호 변경 버튼
        binding.settingsChangePw.setOnClickListener(onClickChangePW)

        // 로그아웃 버튼
        binding.settingsLogout.setOnClickListener{
            logoutDialog.show()
        }

        // 회원탈퇴 버튼
        binding.settingsWithdraw.setOnClickListener{
            withdrawDialog.show()
        }

        // 서점 제보 버튼
        binding.settingsReport.setOnClickListener{
            val reportIntent = Intent(context!!, SettingsReportActivity::class.java)
            startActivity(reportIntent)
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

    // 로그아웃 다이얼로그에 전달할 로그아웃 버튼 리스너
    private val confirmLogout = View.OnClickListener {
        sp.edit().apply{
            this.putString(ApplicationClass.X_ACCESS_TOKEN, null)
            this.putInt("userIdx", -1)
            this.putString("pw", null)
            this.putInt("bidPos", 0)
            this.putInt("smallPos", 0)
            this.putString("selectedLocation", null).apply()
        }

        // 로그인 화면으로 이동
        val loginIntent = Intent(context!!, LoginActivity::class.java)
        startActivity(loginIntent)
        activity!!.finish()
    }

    // 로그아웃 다이얼로그에 전달할 취소 버튼 리스너
    private val cancelLogout = View.OnClickListener{
        logoutDialog.dismiss()
    }

    // 회원탈퇴 다이얼로그에 전달할 버튼 리스너
    private val confirmWithdraw = View.OnClickListener {
        showLoadingDialog(context!!)
        SettingsService(this).tryPatchWithdraw()
    }

    // 회원탈퇴 다이얼로그에 전달할 버튼 리스너
    private val cancelWithdraw = View.OnClickListener{
        withdrawDialog.dismiss()
    }

    override fun onPatchWithdrawSuccess(response: BaseResponse) {
        dismissLoadingDialog()

        when(response.isSuccess){
            true -> {

                // 로그아웃
                sp.edit().apply{
                    this.putString(ApplicationClass.X_ACCESS_TOKEN, null)
                    this.putString("pw", null).apply()
                }

                // 액티비티 닫고, 로그인 화면으로 이동
                parentFragment?.activity!!.apply {
                    val loginActivity = Intent(this, LoginActivity::class.java)
                    startActivity(loginActivity)
                    this.finish()
                }
            }
            else -> showCustomToast("에러가 발생했습니다, 에러가 계속되면 관리자에게 문의해주세요.")

        }
    }

    override fun onPatchWithdrawFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("에러가 발생했습니다, 네트워크 확인 후 에러가 계속되면 관리자에게 문의해주세요.")
    }
}