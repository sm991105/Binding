package com.medium.binding.src.main.my_page.my_post

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.medium.binding.R
import com.medium.binding.config.BaseFragment
import com.medium.binding.databinding.FragmentMyPostBinding
import com.medium.binding.src.main.my_page.models.UserCommentsResponse


class MyPostFragment():
    BaseFragment<FragmentMyPostBinding>(
        FragmentMyPostBinding::bind,
        R.layout.fragment_my_post
), MyPostFragmentView {

    private lateinit var writingRecyclerAdapter: MyWritingRecyclerAdapter

    private var bookIdx: Int? = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        writingRecyclerAdapter = MyWritingRecyclerAdapter(context!!)
        // 리사이클러뷰 초기화
        binding.myPostRecycler.apply {
            this.adapter = writingRecyclerAdapter
            this.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
        }

        // 내가 쓴 글 목록을 불러온다
        bookIdx = arguments?.getInt("bookIdx", -1)
        showLoadingDialog(context!!)
        if(bookIdx != null && bookIdx != -1){
            MyPostService(this).tryGetUserComments(bookIdx!!)
        }else{
            dismissLoadingDialog()

            showCustomToast("내가 쓴 글 정보를 받아오던 중 에러가 발생했습니다\n" +
                    "에러가 계속되면 관리자에게 문의해주세요"
            )
            activity?.onBackPressed()
        }


        // 뒤로가기
        binding.myPostLeft.setOnClickListener{
            activity?.onBackPressed()
        }
    }

    // 내가 쓴 글 API 통신 성공
    override fun onGetUserCommentsSuccess(response: UserCommentsResponse) {
        Log.d("로그", "onGetUserCommentsSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){

            1000 -> {
                val result = response.result

                binding.myPostTitle.text = result.book[0].bookName  // 책 이름 반영
                writingRecyclerAdapter.updateList(result.writing)   // 리사이클러뷰에 데이터 전달
            }

            // 해당 책방이 사라짐
            2000 -> {
                showCustomToast("해당 책방이 현재 존재하지 않습니다")
                activity?.onBackPressed()
            }

            // 책 방에 내 글이 없다
            4000 -> {
                showCustomToast("해당 책방에 내가 쓴 글이 존재하지 않습니다,\n" +
                        "에러가 계속되면 관리자에게 문의해주세요")
            }

            else -> {
                Log.d("로그", "onGetUserCommentsFailure() called, message: ${response.message}")

                showCustomToast("내가 쓴 글 정보를 받아오던 중 에러가 발생했습니다\n" +
                        "에러가 계속되면 관리자에게 문의해주세요"
                )
                activity?.onBackPressed()
            }
        }
    }

    // 내가 쓴 글 API 통신 실패
    override fun onGetUserCommentsFailure(message: String) {
        Log.d("로그", "onGetUserCommentsFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("내가 쓴 글 정보를 받아오던 중 에러가 발생했습니다\n" +
                "에러가 계속되면 관리자에게 문의해주세요"
        )
        activity?.onBackPressed()

    }
}