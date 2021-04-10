package com.medium.binding.src.main.home.room.create

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseFragment
import com.medium.binding.databinding.FragmentHomeCreateBinding
import com.medium.binding.src.main.home.room.HomeRoomActivity
import com.medium.binding.util.Comments
import com.medium.binding.util.General

class HomeCreateFragment(
    private val publishListener: Comments.ClickListener,
    private val comments: String,
    private val commentsFlag: Int,
    val contentsIdx: Int):
    BaseFragment<FragmentHomeCreateBinding>(
        FragmentHomeCreateBinding::bind,
        R.layout.fragment_home_create
){
    private val sp = ApplicationClass.sSharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(commentsFlag == HomeRoomActivity.COMMENTS_EDIT){
            binding.homeRoomCreatePublish.text = String.format("수정")
        }

        // FrameLayout에 띄운 프래그먼트 하단 뷰로 터치 이벤트가 전달되는 것을 막는다
        binding.homeRoomCreateRoot.setOnTouchListener { v, event ->
            v.performClick()
            true
        }

        // 수정하러 들어오면 이전 내용을 보여준다
        binding.homeRoomCreateContent.setText(comments)

        // 뒤로 버튼
        binding.homeRoomCreateCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        // 발행 버튼
        binding.homeRoomCreatePublish.setOnClickListener{

            // 중복 클릭 방지
            if(General.isDoubledClicked()){
                return@setOnClickListener
            }

            val contents = binding.homeRoomCreateContent.text.toString()
            if(contents.length < 5){
                showCustomToast("5자 이상 입력해주세요")
            }
            // 이전 내용과 같은 내용
            else if(comments == contents) {
                activity?.onBackPressed()
            }
            // 발행 요청
            else{
                val comments = binding.homeRoomCreateContent.text.toString()
                publishListener.onClickPub(comments, commentsFlag, contentsIdx)
            }

        }

    }
}