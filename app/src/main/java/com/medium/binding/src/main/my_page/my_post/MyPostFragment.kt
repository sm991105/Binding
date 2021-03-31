package com.medium.binding.src.main.my_page.my_post

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.medium.binding.R
import com.medium.binding.config.BaseFragment
import com.medium.binding.databinding.FragmentMyPostBinding
import com.medium.binding.src.main.my_page.models.UserCommentsResponse


class MyPostFragment(private val writingFlag: Int):
    BaseFragment<FragmentMyPostBinding>(
        FragmentMyPostBinding::bind,
        R.layout.fragment_my_post
), MyPostFragmentView {

    companion object{
        const val MY_POST = 0
        const val BOOKMARK_POST = 1
    }

    private lateinit var commentsRecyclerAdapter: CommentsRecyclerAdapter

    private var bookIdx: Int? = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentsRecyclerAdapter = CommentsRecyclerAdapter(context!!)
        // 리사이클러뷰 초기화
        binding.myPostRecycler.apply {
            this.adapter = commentsRecyclerAdapter
            this.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
        }

        // 내가 쓴 글 목록을 불러온다
        bookIdx = arguments?.getInt("bookIdx", -1)
        showLoadingDialog(context!!)
        Log.d("로그", "flag: $writingFlag")
        if(bookIdx != null && bookIdx != -1){

            // 내가 쓴 글을 타고 들어왔을 때
           if(writingFlag == MY_POST){
                MyPostService(this).tryGetUserComments(bookIdx!!)
           }

           // 북마크한 글을 타고 들어왔을 때
           else{
                MyPostService(this).tryGetMarkComments(bookIdx!!)
           }
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
        dismissLoadingDialog()

        when(response.code){

            1000 -> {
                val result = response.result

                binding.myPostTitle.text = result.book[0].bookName  // 책 이름 반영
                commentsRecyclerAdapter.updateList(result.writing)   // 리사이클러뷰에 데이터 전달
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
                showCustomToast("내가 쓴 글 정보를 받아오던 중 에러가 발생했습니다\n" +
                        "에러가 계속되면 관리자에게 문의해주세요"
                )
                activity?.onBackPressed()
            }
        }
    }

    // 내가 쓴 글 API 통신 실패
    override fun onGetUserCommentsFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("내가 쓴 글 정보를 받아오던 중 에러가 발생했습니다\n" +
                "네트워크 확인 후 에러가 계속되면 관리자에게 문의해주세요"
        )
        activity?.onBackPressed()

    }

    // 북마크한 글 API 통신 성공
    override fun onGetMarkCommentsSuccess(response: UserCommentsResponse) {
        dismissLoadingDialog()

        when(response.code){

            1000 -> {
                val result = response.result

                binding.myPostTitle.text = result.book[0].bookName  // 책 이름 반영
                commentsRecyclerAdapter.updateList(result.writing)   // 리사이클러뷰에 데이터 전달
            }

            // 해당 책방이 사라짐
            2000 -> {
                showCustomToast("해당 책방이 현재 존재하지 않습니다")
                activity?.onBackPressed()
            }

            // 책 방에 내 글이 없다
            4000 -> {
                showCustomToast("해당 책방에 북마크한 글이 존재하지 않습니다,\n" +
                        "에러가 계속되면 관리자에게 문의해주세요")
                activity?.onBackPressed()
            }

            else -> {
                showCustomToast("북마크한 글 정보를 받아오던 중 에러가 발생했습니다\n" +
                        "에러가 계속되면 관리자에게 문의해주세요"
                )
                activity?.onBackPressed()
            }
        }
    }

    // 내가 쓴 글 API 통신 실패
    override fun onGetMarkCommentsFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("북마크한 글 정보를 받아오던 중 에러가 발생했습니다\n" +
                "네트워크 확인 후 에러가 계속되면 관리자에게 문의해주세요"
        )
        activity?.onBackPressed()

    }
}