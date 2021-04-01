package com.medium.binding.src.main.my_page.my_post

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseFragment
import com.medium.binding.config.BaseResponse
import com.medium.binding.databinding.FragmentMyPostBinding
import com.medium.binding.src.main.home.models.CommentsBody
import com.medium.binding.src.main.home.models.ReportBody
import com.medium.binding.src.main.home.room.HomeRoomActivity
import com.medium.binding.src.main.my_page.models.UserCommentsResponse
import com.medium.binding.util.Comments


class MyPostFragment(private val writingFlag: Int):
    BaseFragment<FragmentMyPostBinding>(
        FragmentMyPostBinding::bind,
        R.layout.fragment_my_post
), MyPostFragmentView, Comments.CommentsView, Comments.ClickListener{

    companion object{
        const val MY_POST = 0
        const val BOOKMARK_POST = 1
    }

    private lateinit var commentsRecyclerAdapter: CommentsRecyclerAdapter

    private var bookIdx: Int? = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentsRecyclerAdapter = CommentsRecyclerAdapter(this, context!!,this)
        // 리사이클러뷰 초기화
        binding.myPostRecycler.apply {
            this.adapter = commentsRecyclerAdapter
            this.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
        }

        // 글 목록을 불러온다
        bookIdx = arguments?.getInt("bookIdx", -1)
        showLoadingDialog(context!!)
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
                showCustomToast("해당 책방이 존재하지 않습니다")
                activity?.onBackPressed()
            }

            // 책 방에 내 글이 없다
            4000 -> showCustomToast("책방에 내가 쓴 글이 존재하지 않습니다")

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

                binding.myPostTitle.text = result.book[0].bookName   // 책 이름 반영
                commentsRecyclerAdapter.updateList(result.writing)   // 리사이클러뷰에 데이터 전달
            }

            // 해당 책방이 사라짐
            2000 -> {

                // 책방 글 수정,삭제 후에 호출했으면, 새로고침을 한다
                if(ApplicationClass.isCommentsChanged){
                    commentsRecyclerAdapter.updateList(response.result.writing)
                }
                showCustomToast("해당 책방이 현재 존재하지 않습니다")
                ApplicationClass.isEdited = true
                activity?.onBackPressed()
            }

            // 책 방에 내 글이 없다
            4000 -> {

                // 책방 글 수정,삭제 후에 호출했으면, 새로고침을 한다
                if(ApplicationClass.isCommentsChanged){
                    commentsRecyclerAdapter.updateList(response.result.writing)
                }
                showCustomToast("북마크한 글이 현재 존재하지 않습니다")
                ApplicationClass.isEdited = true
                activity?.onBackPressed()
            }

            else -> {
                showCustomToast("북마크한 글 정보를 받아오던 중 에러가 발생했습니다\n" +
                        "에러가 계속되면 관리자에게 문의해주세요"
                )
                activity?.onBackPressed()
            }
        }
        ApplicationClass.isCommentsChanged = false
    }

    // 내가 쓴 글 API 통신 실패
    override fun onGetMarkCommentsFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("북마크한 글 정보를 받아오던 중 에러가 발생했습니다\n" +
                "네트워크 확인 후 에러가 계속되면 관리자에게 문의해주세요"
        )
        activity?.onBackPressed()

    }

    // 다이얼로그에서 삭제 버튼을 누르면, 어댑터에서 실행할 콜백 함수
    override fun onClickRemove(contentsIdx: Int) {
        // 글 삭제 요청
        if(contentsIdx != -1){
            Comments.CommentsService(this).tryDeleteComments(bookIdx!!, contentsIdx)
        }else{
            showCustomToast("글 삭제를 요청하던 중 오류가 발생했습니다\n" +
                    "네트워크 확인 후 오류가 계속되면 관리자에게 문의해주세요.")
        }
    }

    // 내가 쓴 책방 글 삭제 통신 성공
    override fun onDeleteCommentsSuccess(response: BaseResponse) {
        dismissLoadingDialog()
        commentsRecyclerAdapter.removeDialog?.dismiss()

        when(response.code) {
            1000 -> {
                ApplicationClass.isCommentsChanged = true
                showCustomToast("글이 삭제되었습니다")
                context?.let{
                    showLoadingDialog(context!!)
                MyPostService(this).tryGetUserComments(bookIdx!!)}  // 새로고침
            }

            3000 -> {
                showCustomToast("해당 책방이 존재하지 않습니다")
                ApplicationClass.isEdited = true
                activity?.onBackPressed()
            }

            3001 -> {
                showCustomToast("해당 글이 존재하지 않습니다")
                context?.let{
                    showLoadingDialog(context!!)
                    MyPostService(this).tryGetUserComments(bookIdx!!)}  // 새로고침
            }

            else -> {
                showCustomToast("글 삭제 중 오류가 발생했습니다\n" +
                        "오류가 계속되면 관리자에게 문의주세요.")
            }
        }
    }

    // 내가 쓴 책방 글 삭제 통신 실패
    override fun onDeleteCommentsFailure(message: String) {
        dismissLoadingDialog()
        commentsRecyclerAdapter.removeDialog?.dismiss()

        showCustomToast("글 삭제 중 오류가 발생했습니다\n" +
                "오류가 계속되면 관리자에게 문의주세요.")
    }

    // 다이얼로그에서 신고 버튼을 누르면, 어댑터에서 실행할 콜백 함수
    override fun onClickReport(reportReason: String, contentIdx: Int) {
        if(contentIdx != -1){
            val reportBody = ReportBody(reportReason)
            Comments.CommentsService(this).tryPostReport(bookIdx!!, contentIdx, reportBody)
        }else{
            showCustomToast("글 신고 중 오류가 발생했습니다\n" +
                    "오류가 계속되면 관리자에게 문의주세요")
        }
    }

    // 책방 글 신고 통신 성공
    override fun onPostReportSuccess(response: BaseResponse) {
        dismissLoadingDialog()

        when(response.code) {
            1000 -> {
                commentsRecyclerAdapter.reportDialog?.dismiss()
                showCustomToast("글이 신고되었습니다")
            }

            2001 -> showCustomToast("5자 이상 적어주세요")

            2002 -> showCustomToast("500자 이하로 입력하세요")

            3000 -> {
                showCustomToast("해당 책방이 이제 존재하지 않습니다")
                commentsRecyclerAdapter.reportDialog?.dismiss()
                ApplicationClass.isEdited = true
            }

            3001 -> {
                showCustomToast("해당 글이 이제 존재하지 않습니다")
                showLoadingDialog(context!!)
                ApplicationClass.isCommentsChanged = true
                MyPostService(this).tryGetUserComments(bookIdx!!)
            }

            3004 -> {
                showCustomToast("이미 신고한 글입니다")
            }

            else -> {
                showCustomToast("글 신고 중 오류가 발생했습니다\n" +
                        "오류가 계속되면 관리자에게 문의주세요.")
            }
        }
    }

    // 책방 글 신고 통신 실패
    override fun onPostReportFailure(message: String) {
        dismissLoadingDialog()
        commentsRecyclerAdapter.reportDialog?.dismiss()

        showCustomToast("글 신고 중 오류가 발생했습니다\n" +
                "네트워크 확인 후 오류가 계속되면 관리자에게 문의주세요.")
    }

    // 책방 글쓰는 화면에서 발행버튼을 눌렀을 떄
    override fun onClickPub(comments: String, commentsFlag: Int, contentsIdx: Int){
        context?.let{
            showLoadingDialog(it)
            val commentsBody = CommentsBody(comments)

            when(commentsFlag){

                // 글 수정
                HomeRoomActivity.COMMENTS_EDIT -> {
                    Comments.CommentsService(this).tryPatchComments(bookIdx!!, contentsIdx, commentsBody)
                }
            }
        }
    }

    // 글 수정 통신 성공
    override fun onPatchCommentsSuccess(response: BaseResponse) {
        dismissLoadingDialog()

        when(response.code) {

            // 글 수정 성공
            1000 -> {
                showCustomToast("글이 수정되었습니다")
                activity?.onBackPressed()

                // 새로고침
                context?.let {

                    if (bookIdx != -1) {
                        MyPostService(this).tryGetUserComments(bookIdx!!)
                    } else {
                        null
                    }
                }
            }

            2001 -> showCustomToast("5자 이상 적어주세요")

            3000 -> {
                showCustomToast("해당 책방이 이제 존재하지 않습니다")
            }

            3001 -> {
                showCustomToast("해당 글이 존재하지 않습니다, 자세한 사항은 관리자에게 문의해주세요.")
            }

            else -> {
                showCustomToast("글 발행 중 오류가 발생했습니다\n" +
                        "오류가 계속되면 관리자에게 문의주세요.")
            }
        }
    }

    // 글 수정 통신 실패
    override fun onPatchCommentsFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("글 수정 중 오류가 발생했습니다\n" +
                "네트워크 확인 후 오류가 계속되면 관리자에게 문의주세요.")
    }

}