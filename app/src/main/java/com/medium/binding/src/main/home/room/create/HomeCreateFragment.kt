package com.medium.binding.src.main.home.room.create

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseFragment
import com.medium.binding.databinding.FragmentHomeCreateBinding
import com.medium.binding.src.main.home.models.CommentsBody
import com.medium.binding.src.main.home.room.HomeRoomActivityView

class HomeCreateFragment(val homeRoomActivity: HomeRoomActivityView):
    BaseFragment<FragmentHomeCreateBinding>(
        FragmentHomeCreateBinding::bind,
        R.layout.fragment_home_create
){
    private val sp = ApplicationClass.sSharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FrameLayout에 띄운 프래그먼트 하단 뷰로 터치 이벤트가 전달되는 것을 막는다
        binding.homeRoomCreateRoot.setOnTouchListener { v, event ->
            v.performClick()
            true
        }

        binding.homeRoomCreateCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.homeRoomCreatePublish.setOnClickListener{

            // 중복 클릭 방지
            ApplicationClass.mLastClickTime.apply{
                if (SystemClock.elapsedRealtime() - ApplicationClass.mLastClickTime.toInt() < 1000){
                    return@setOnClickListener
                }
                this.compareAndSet(this.toLong(), SystemClock.elapsedRealtime())
            }

            val contents = binding.homeRoomCreateContent.text.toString()
            if(contents.length < 5){
                showCustomToast("5자 이상 입력해주세요")
            }else{
                val commentsBody = CommentsBody(binding.homeRoomCreateContent.text.toString())
                homeRoomActivity.onClickPub(commentsBody)
            }

        }

    }
}