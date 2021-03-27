package com.makeus6.binding.src.main.home.room

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.makeus6.binding.config.BaseActivity
import com.makeus6.binding.databinding.ActivityHomeRoomBinding
import com.makeus6.binding.src.main.home.models.CommentsResult
import com.makeus6.binding.src.main.home.models.GetCommentsResponse

class HomeRoomActivity : BaseActivity<ActivityHomeRoomBinding>(ActivityHomeRoomBinding::inflate),
HomeRoomActivityView{

    // 뒤로가기 2번 눌러 종료할 때 사용
    private val FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0

    private var bookIdx: Int? = null
    private var bookTitle: String? = null

    lateinit var commentsRecyclerAdapter: CommentsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 코멘트 어댑터
        commentsRecyclerAdapter = CommentsRecyclerAdapter(this)
        binding.homeRoomRecycler.apply {
            adapter = commentsRecyclerAdapter
            layoutManager = LinearLayoutManager(this@HomeRoomActivity,
                LinearLayoutManager.VERTICAL, false
            )
        }

        // 불러올 책방 인덱스
        bookIdx = intent.extras?.getInt("bookIdx")
        Log.d("로그", "bookIdx: $bookIdx")
        bookIdx?.let{
            showLoadingDialog(this)
            HomeRoomService(this).tryGetNewestWR(bookIdx!!)
        }
    }

    // 두 번 눌러 앱 종료
    private fun finishOnBackPressed() {
        val tempTime: Long = System.currentTimeMillis()       // 현재 시간과 1970년 1월 1일 시간 차
        val intervalTime: Long = tempTime - backPressedTime

        if(intervalTime >= 0 && FINISH_INTERVAL_TIME >= intervalTime){
            super.onBackPressed()
            Log.d("로그", "onBackPressed() called")
        }else{
            backPressedTime = tempTime
            showCustomToast("뒤로 버튼을 한번 더 누르시면 종료됩니다.")
        }
    }

    // 책방 댓글 최신순 불러오기 통신 성공
    override fun onGetNewestWRSuccess(response: GetCommentsResponse) {
        Log.d("로그", "onGetNewestWRSuccess() called, response: $response")

        val result = response.result
        Log.d("로그", "bookName: $result")

        dismissLoadingDialog()


        when(response.code){
            // 성공
            1000 -> {
                Log.d("로그", "최신순 글 조회 성공")

                doWhenSuccess(response.result)
            }

            else -> {
                Log.d("로그", "message: ${response.message}")

                showCustomToast("책방 글을 불러오던 중 에러가 발생했습니다\n" +
                        "에러가 계속되면 관리자에게 문의주세요.")
            }
        }
    }

    // 책방 댓글 최신순 불러오기 통신 실패
    override fun onGetNewestWRFailure(message: String) {
        Log.d("로그", "onGetNewestWRFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("책방 글을 불러오던 중 에러가 발생했습니다\n" +
                "에러가 계속되면 관리자에게 문의주세요.")
    }

    // 책방 댓글 북마크순 불러오기 통신 성공
    override fun onGetMarkedWRSuccess(response: GetCommentsResponse) {
        Log.d("로그", "onGetMarkedWRSuccess() called, response: $response")

        val result = response.result
        Log.d("로그", "bookName: $result")

        dismissLoadingDialog()


        when(response.code){
            // 성공
            1000 -> {
                Log.d("로그", "북마크순 글 조회 성공")

                doWhenSuccess(response.result)
            }

            else -> {
                Log.d("로그", "message: ${response.message}")

                showCustomToast("책방 글을 불러오던 중 에러가 발생했습니다\n" +
                        "에러가 계속되면 관리자에게 문의주세요.")
            }
        }
    }

    // 책방 댓글 북마크순 불러오기 통신 실패
    override fun onGetMarkedWRFailure(message: String) {
        Log.d("로그", "onGetMarkedWRFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("책방 글을 불러오던 중 에러가 발생했습니다\n" +
                "에러가 계속되면 관리자에게 문의주세요.")
    }

    private fun doWhenSuccess(result: ArrayList<CommentsResult>){
        bookTitle = result[0].bookName
        result.removeAt(0)
        with(bookTitle){
            binding.homeRoomTitle.text = this
            binding.homeRoomTextTitle.text = this
        }

        commentsRecyclerAdapter.updateList(result)
    }
}