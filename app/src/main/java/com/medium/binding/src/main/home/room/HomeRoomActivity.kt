package com.medium.binding.src.main.home.room

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.medium.binding.R
import com.medium.binding.config.BaseActivity
import com.medium.binding.config.BaseResponse
import com.medium.binding.databinding.ActivityHomeRoomBinding
import com.medium.binding.src.main.home.HomeFragment
import com.medium.binding.src.main.home.models.CommentsBody
import com.medium.binding.src.main.home.models.CommentsResult
import com.medium.binding.src.main.home.models.GetCommentsResponse
import com.medium.binding.src.main.home.models.ReportBody
import com.medium.binding.src.main.home.room.create.HomeCreateFragment
import com.medium.binding.util.Comments
import kotlinx.android.synthetic.main.item_bookmark_store.*

class HomeRoomActivity:
    BaseActivity<ActivityHomeRoomBinding>(ActivityHomeRoomBinding::inflate),
HomeRoomActivityView, Comments.CommentsView, Comments.ClickListener{

    companion object{
        // 뒤로가기 2번 눌러 종료할 때 사용
        private const val FINISH_INTERVAL_TIME: Long = 2000
        private const val ORDER_BY_BOOKMARK: Int = 0
        private const val ORDER_BY_NEWEST: Int = 1
        private var sortFlag: Int = ORDER_BY_BOOKMARK   // 0 - 북마크순, 1 - 최신순
        const val BOOKMARK_ON: Int = 1
        const val BOOKMARK_OFF: Int = 0
        const val COMMENTS_CREATE: Int = 0      // 글 발행
        const val COMMENTS_EDIT: Int = 1        // 글 수정
    }

    // 뒤로가기 2번 눌러 종료할 때 사용
    private var backPressedTime: Long = 0

    private var bookIdx: Int? = null
    private var bookTitle: String? = null

    lateinit var commentsRecyclerAdapter: CommentsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_OK)

        // 액션 바 설정
        setSupportActionBar(binding.homeRoomToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 정렬탭 레이아웃 테두리 둥글게 만들기 위함
        binding.homeRoomSortTab.clipToOutline = true

        // 코멘트 어댑터
        commentsRecyclerAdapter = CommentsRecyclerAdapter(
            this, this, this
        )
        binding.homeRoomRecycler.apply {
            adapter = commentsRecyclerAdapter
            layoutManager = LinearLayoutManager(
                this@HomeRoomActivity,
                LinearLayoutManager.VERTICAL, false
            )
        }

        // 불러올 책방 인덱스
        bookIdx = intent.extras?.getInt("bookIdx")
        bookIdx?.let {
            showLoadingDialog(this)
            HomeRoomService(this).tryGetNewestWR(bookIdx!!)
        }

        // 뒤로가기 버튼
        binding.homeRoomLeft.setOnClickListener {
            super.onBackPressed()
        }

        // 정렬 탭 버튼
        binding.homeRoomSortBtn.setOnClickListener(onClickSort)
        // 정렬 버튼
        binding.homeRoomSortBookmark.setOnClickListener(onClickSortBookmark)
        binding.homeRoomSortNewest.setOnClickListener(onClickSortNewest)

        binding.homeRoomRecycler.setOnClickListener {
            if (binding.homeRoomSortTab.visibility == View.VISIBLE) {
                binding.homeRoomSortTab.visibility = View.INVISIBLE
            }
        }
    }

    // 정렬 탭 버튼 리스너
    private val onClickSort = View.OnClickListener {
        binding.homeRoomSortTab.apply{
            if(this.visibility == View.INVISIBLE){
                this.visibility = View.VISIBLE
            }else{
                this.visibility = View.INVISIBLE
            }
        }
    }

    // 북마크순 정렬 버튼 리스너
    private val onClickSortBookmark = View.OnClickListener {
        if(sortFlag != ORDER_BY_BOOKMARK){
            showLoadingDialog(this)
            HomeRoomService(this).tryGetMarkedWR(bookIdx!!)
        }else{
            binding.homeRoomSortTab.visibility = View.INVISIBLE
        }
    }

    // 최신순 정렬 버튼 리스너
    private val onClickSortNewest = View.OnClickListener {
        if(sortFlag != ORDER_BY_NEWEST){
            showLoadingDialog(this)
            HomeRoomService(this).tryGetNewestWR(bookIdx!!)
        }else{
            binding.homeRoomSortTab.visibility = View.INVISIBLE
        }
    }

    // 두 번 눌러 앱 종료
    private fun finishOnBackPressed() {
        val tempTime: Long = System.currentTimeMillis()       // 현재 시간과 1970년 1월 1일 시간 차
        val intervalTime: Long = tempTime - backPressedTime

        if(intervalTime >= 0 && FINISH_INTERVAL_TIME >= intervalTime){
            super.onBackPressed()
        }else{
            backPressedTime = tempTime
            showCustomToast("뒤로 버튼을 한번 더 누르시면 종료됩니다.")
        }
    }

    // 책방 댓글 최신순 불러오기 통신 성공
    override fun onGetNewestWRSuccess(response: GetCommentsResponse) {
        val result = response.result

        dismissLoadingDialog()

        when(response.code){
            // 성공
            1000 -> doWhenSuccess(result, ORDER_BY_NEWEST)

            // 책방이 없어졌으면 메시지를 띄우고 홈으로 나간다
            3000 -> whenBookRoomRemoved()

            else -> {
                showCustomToast("책방 글을 불러오던 중 오류가 발생했습니다\n" +
                        "오류가 계속되면 관리자에게 문의주세요.")
                setResult(HomeFragment.BOOK_REMOVED)
                finish()
            }
        }
    }

    // 책방 댓글 최신순 불러오기 통신 실패
    override fun onGetNewestWRFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("책방 글을 불러오던 중 오류가 발생했습니다\n" +
                "네트워크 확인 후 에러가 계속되면 관리자에게 문의주세요.")
        setResult(HomeFragment.BOOK_REMOVED)
        finish()
    }

    // 책방 댓글 북마크순 불러오기 통신 성공
    override fun onGetMarkedWRSuccess(response: GetCommentsResponse) {
        val result = response.result
        dismissLoadingDialog()

        when(response.code){
            // 성공
            1000 -> doWhenSuccess(result, ORDER_BY_BOOKMARK)

            // 삭제된 책방이면 메시지를 띄우고 나간다
            3000 -> whenBookRoomRemoved()

            else -> {
                showCustomToast("책방 글을 불러오던 중 오류가 발생했습니다\n" +
                        "오류가 계속되면 관리자에게 문의주세요.")
                setResult(HomeFragment.BOOK_REMOVED)
                finish()
            }
        }
    }

    // 책방 댓글 북마크순 불러오기 통신 실패
    override fun onGetMarkedWRFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("책방 글을 불러오던 중 에러가 발생했습니다\n" +
                "네트워크 확인 후 에러가 계속되면 관리자에게 문의주세요.")
    }

    // 글 북마크 수정 통신 성공
    override fun onPatchWBookmarkSuccess(response: BaseResponse, itemPos: Int) {
        dismissLoadingDialog()

        when(response.code){

            // 성공(추가 or ON)
            in 1000..1001 -> {
                commentsRecyclerAdapter.updateItem(itemPos, BOOKMARK_ON)
            }

            // 성공(해제)
            1002 -> {
                commentsRecyclerAdapter.updateItem(itemPos, BOOKMARK_OFF)
            }

            // 책방이 사라졌으면 메시지를 띄우고 홈으로 나간다
            3001 -> whenBookRoomRemoved()

            else-> {
                showCustomToast("북마크 수정 중 오류가 발생했습니다\n" +
                        "오류가 계속되면 관리자에게 문의주세요.")
                setResult(HomeFragment.BOOK_REMOVED)
            }

        }
    }

    // 글 북마크 수정 통신 실패
    override fun onPatchWBookmarkFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("북마크 수정 중 오류가 발생했습니다\n" +
                "네트워크 확인 후 오류가 계속되면 관리자에게 문의주세요.")
        setResult(HomeFragment.BOOK_REMOVED)
        finish()
    }

    // 책방 글 조회 성공 -> 제목, 책방 댓글을 보여준다
    private fun doWhenSuccess(result: ArrayList<CommentsResult>, mSortFlag: Int){
        bookTitle = result[0].bookName

        // 책 제목
        with(bookTitle){
            binding.homeRoomTitle.text = this
            binding.homeRoomTextTitle.text = this
        }

        result.removeAt(0)
        commentsRecyclerAdapter.updateList(result)

        binding.homeRoomSortTab.visibility = View.INVISIBLE

        // 탭 Text 설정
        when(mSortFlag){
            ORDER_BY_BOOKMARK -> {
                binding.homeRoomSortText.text = String.format("북마크순")
                sortFlag = ORDER_BY_BOOKMARK

            }
            ORDER_BY_NEWEST -> {
                binding.homeRoomSortText.text = String.format("최신순")
                sortFlag = ORDER_BY_NEWEST
            }
        }
    }

    // 화면을 터치하면 정렬탭이 닫힌다
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if(binding.homeRoomSortTab.visibility == View.VISIBLE){
                        binding.homeRoomSortTab.visibility = View.INVISIBLE
                    }
            }
        }
        return true
    }

        if(binding.homeRoomSortTab.visibility == View.VISIBLE){
            binding.homeRoomSortTab.visibility = View.INVISIBLE
            return false
        }

        return super.onTouchEvent(event)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_home_room_toolbar, menu)

        return true
    }

    // 툴바 아이템 눌렀을 떄
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_home_room_toolbar_write -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.home_room_frm, HomeCreateFragment(
                        this, "", COMMENTS_CREATE, -1))
                    .addToBackStack("HomeCreate")
                    .commitAllowingStateLoss()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 책방 글쓰는 화면에서 발행버튼을 눌렀을 떄
    override fun onClickPub(comments: String, commentsFlag: Int, contentsIdx: Int){
        showLoadingDialog(this)
        val commentsBody = CommentsBody(comments)

        when(commentsFlag){

            // 글 발행
            COMMENTS_CREATE-> HomeRoomService(this).tryPostComments(bookIdx!!, commentsBody)

            // 글 수정
            COMMENTS_EDIT -> {
                Comments.CommentsService(this).tryPatchComments(bookIdx!!, contentsIdx, commentsBody)
            }
        }
    }

    // 글 발행 통신 성공
    override fun onPostCommentsSuccess(response: BaseResponse) {
        dismissLoadingDialog()

        when(response.code) {
            1000 -> {
                showCustomToast("글이 발행되었습니다")
                reflectCommentsUpdate()
            }

            2001 -> showCustomToast("5자 이상 적어주세요")

            else -> {
                showCustomToast("글 발행 중 오류가 발생했습니다\n" +
                        "오류가 계속되면 관리자에게 문의주세요.")
                setResult(HomeFragment.BOOK_REMOVED)
            }
        }
    }

    // 글 발행 통신 실패
    override fun onPostCommentsFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("글 발행 중 오류가 발생했습니다\n" +
                "네트워크 확인 후 오류가 계속되면 관리자에게 문의주세요.")
        setResult(HomeFragment.BOOK_REMOVED)
    }

    // 글 수정 통신 성공
    override fun onPatchCommentsSuccess(response: BaseResponse) {
        dismissLoadingDialog()

        when(response.code) {
            1000 -> {
                showCustomToast("글이 수정되었습니다")
                reflectCommentsUpdate()
            }

            2001 -> showCustomToast("5자 이상 적어주세요")

            3000 -> {
                showCustomToast("해당 책방이 존재하지 않습니다")
                setResult(HomeFragment.BOOK_REMOVED)
            }

            3001 -> {
                showCustomToast("해당 글이 존재하지 않습니다")
                reflectCommentsUpdate()
            }

            else -> {
                showCustomToast("글 발행 중 오류가 발생했습니다\n" +
                        "오류가 계속되면 관리자에게 문의주세요.")
                setResult(HomeFragment.BOOK_REMOVED)
            }
        }
    }

    // 글 수정 통신 실패
    override fun onPatchCommentsFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("글 수정 중 오류가 발생했습니다\n" +
                "네트워크 확인 후 오류가 계속되면 관리자에게 문의주세요.")
        setResult(HomeFragment.BOOK_REMOVED)
    }

    // 글 발행 or 수정사항 반영
    private fun reflectCommentsUpdate(){
        super.onBackPressed()
        showLoadingDialog(this)
        HomeRoomService(this).tryGetNewestWR(bookIdx!!)
    }

    // 통신에서 책방이 없다고 올 때
    private fun whenBookRoomRemoved(){
        showCustomToast("해당 책방이 존재하지 않습니다")
        setResult(HomeFragment.BOOK_REMOVED)
    }

    // 다이얼로그에서 글 삭제버튼을 눌렀을 때
    override fun onClickRemove(contentIdx: Int) {
        if(contentIdx != -1){
            Comments.CommentsService(this).tryDeleteComments(bookIdx!!, contentIdx)
        }else{
            showCustomToast("잠시 후 다시 시도해주세요, 오류가 계속될 경우 관리자에게 문의해주세요")
        }
    }

    // 책방 글 삭제 통신 성공
    override fun onDeleteCommentsSuccess(response: BaseResponse) {
        dismissLoadingDialog()

        when(response.code) {
            1000 -> {
                commentsRecyclerAdapter.removeDialog.dismiss()
                showCustomToast("글이 삭제되었습니다")
                showLoadingDialog(this)
                HomeRoomService(this).tryGetNewestWR(bookIdx!!)
            }

            3000 -> {
                showCustomToast("해당 책방이 존재하지 않습니다")
                setResult(HomeFragment.BOOK_REMOVED)
                finish()
            }

            3001 -> {
                showCustomToast("해당 글이 존재하지 않습니다")
                showLoadingDialog(this)
                HomeRoomService(this).tryGetNewestWR(bookIdx!!)
            }

            else -> {
                showCustomToast("글 삭제 중 오류가 발생했습니다\n" +
                        "오류가 계속되면 관리자에게 문의주세요.")
                setResult(HomeFragment.BOOK_REMOVED)
            }
        }
    }

    // 책방 글 삭제 통신 실패
    override fun onDeleteCommentsFailure(message: String) {
        dismissLoadingDialog()
        commentsRecyclerAdapter.removeDialog.dismiss()

        showCustomToast("글 삭제 중 에러가 발생했습니다\n" +
                "에러가 계속되면 관리자에게 문의주세요.")
        setResult(HomeFragment.BOOK_REMOVED)
    }

    override fun onClickReport(reportReason: String, contentIdx: Int) {
        if(contentIdx != -1){
            val reportBody = ReportBody(reportReason)
            Comments.CommentsService(this).tryPostReport(bookIdx!!, contentIdx, reportBody)
        }else{
            showCustomToast("잠시 후 다시 시도해주세요.")
        }
    }

    // 책방 글 신고 통신 성공
    override fun onPostReportSuccess(response: BaseResponse) {
        dismissLoadingDialog()

        when(response.code) {
            1000 -> {
                commentsRecyclerAdapter.reportDialog.dismiss()
                showCustomToast("글이 신고되었습니다")
            }

            2001 -> showCustomToast("5자 이상 적어주세요")

            2002 -> showCustomToast("500자 이하로 입력하세요")

            3000 -> {
                showCustomToast("해당 책방이 존재하지 않습니다")
                setResult(HomeFragment.BOOK_REMOVED)
                finish()
            }

            3001 -> {
                showCustomToast("해당 글이 존재하지 않습니다")
                showLoadingDialog(this)
                HomeRoomService(this).tryGetNewestWR(bookIdx!!)
            }

            3004 -> {
                showCustomToast("이미 신고한 글입니다")
            }

            else -> {
                showCustomToast("글 신고 중 오류가 발생했습니다\n" +
                        "오류가 계속되면 관리자에게 문의주세요.")
                setResult(HomeFragment.BOOK_REMOVED)
            }
        }
    }

    // 책방 글 신고 통신 실패
    override fun onPostReportFailure(message: String) {
        dismissLoadingDialog()
        commentsRecyclerAdapter.reportDialog.dismiss()

        showCustomToast("글 신고 중 오류가 발생했습니다\n" +
                "네트워크 확인 후 오류가 계속되면 관리자에게 문의주세요.")
        setResult(HomeFragment.BOOK_REMOVED)
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 0){
            supportFragmentManager.popBackStack()
        }else{
            super.onBackPressed()
        }
    }
}