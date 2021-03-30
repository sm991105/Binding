package com.medium.binding.src.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.view.animation.AnimationUtils
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseActivity
import com.medium.binding.databinding.ActivityAfterSplashBinding
import com.medium.binding.src.login.LoginActivity
import com.medium.binding.src.main.MainActivity

class SplashActivity : BaseActivity<ActivityAfterSplashBinding>(ActivityAfterSplashBinding::inflate) {

    private val sp = ApplicationClass.sSharedPreferences

    // 버튼 중복클릭 방지용
    private var mLastClickTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jwt = sp.getString(ApplicationClass.X_ACCESS_TOKEN, null)

        // 처음에 next 버튼을 안보이게 한다
        binding.splashNext.visibility = View.GONE

        // 점점 안보이던 버튼을 선명하게 해주는 애니메이션
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.splash_alpha)

        // {}초 후, 애니메이션 적용
        Handler(Looper.getMainLooper()).postDelayed({

            // 로그인이 안되어 있으면 넥스트 버튼을 띄운다
            if (jwt == null) {
                binding.splashNext.startAnimation(animation)
                binding.splashNext.visibility = View.VISIBLE
            }
            // 로그인 되어있으면 메인 화면으로 넘어간다
            else {
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
                overridePendingTransition(R.anim.none, R.anim.none_to_left)
                finish()
            }
        }, 1500)

        binding.splashNext.setOnClickListener(onNextClick)
    }

    // 다음 아이콘을 클릭하면 로그인 화면으로 이동한다.
    private val onNextClick = View.OnClickListener {

        ApplicationClass.mLastClickTime.apply{
            if (SystemClock.elapsedRealtime() - ApplicationClass.mLastClickTime.toInt() < 1000){
                return@OnClickListener
            }
            this.compareAndSet(this.toLong(), SystemClock.elapsedRealtime())
        }

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        overridePendingTransition(R.anim.none, R.anim.none_to_left)
        finish()
    }
}