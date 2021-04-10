package com.medium.binding.src.main.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medium.binding.R
import com.medium.binding.config.BaseFragment
import com.medium.binding.databinding.FragmentHomeBinding
import com.medium.binding.src.main.home.create_room.CreateBookDialog
import com.medium.binding.src.main.home.models.*
import com.medium.binding.util.General
import java.util.*


class HomeFragment: BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
), HomeFragmentView, SearchView.OnQueryTextListener{

    companion object{
        const val BOOK_ENTERED_CODE = 2000  // 책방 열기
        const val BOOK_REMOVED = 1000   // 삭제된 책방
        const val ORDER_BY_NEWEST = 0   // 최신글 순
        const val ORDER_BY_POPULAR = 1 // 인기글 순
        var categoryFlag = ORDER_BY_NEWEST // 기본값: 최신글 순
        const val LIMIT = 25
    }

    private var newestBooksList = ArrayList<NewestResult>()
    private var popularBooksList = ArrayList<PopularResult>()
    lateinit var homeRecyclerAdapter: HomeRecyclerViewAdapter

    lateinit var mMenuItem: MenuItem
    private lateinit var mSearchView: SearchView
    private lateinit var searchEditText: EditText

    private var hasMore = true      // 서버에서 불러올 데이터가 더 있는지
    private var page = 0            // 카테고리 내 현재 페이지
    private var searchPage = 0      // 검색할 때 사용할 페이지
    private var isLoading = false   // 서버와 통신중인지
    private var isSearching = false // 검색중인지
    private var bookQuery = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 책방 리사이클러 뷰 초기화
        homeRecyclerAdapter = HomeRecyclerViewAdapter(
            this,
            newestBooksList, popularBooksList, categoryFlag
        )
        binding.homeRecycler.apply{
            this.adapter = homeRecyclerAdapter
            this.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
            this.addOnScrollListener(onRecyclerScroll)  // 무한 스크롤
        }

        // 책방 불러오기
        initBookRooms()

        // 글 카테고리 선택(탭) 버튼 클릭
        binding.homeSortBtn.setOnClickListener(onClickTab)

        // sort tab 안에서 최신글 / 인기글 클릭 -> 필터링 적용
        binding.homeSortNewest.setOnClickListener(onClickNewest)
        binding.homeSortPopular.setOnClickListener(onClickPopular)

        // 카테고리 선택 창이 열려있을 때 RecyclerView를 터치하면 item이 터치되지 않고,
        // 선택 창이 닫힌다.
        binding.homeRecycler.setOnTouchListener(onTouchRecyclerView)

        // 책방 만들기 버튼 클릭
        binding.homeCreateRoom.setOnClickListener(onClickCreateRoom)

        // 액션바 설정
        val toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.menu_home_toolbar)
        mMenuItem = toolbar.menu.findItem(R.id.menu_home_toolbar_search)
        mSearchView = mMenuItem.actionView as SearchView

        // 서치뷰에서 뒤로가기 버튼 눌렀을 때 동작 정의하기 위함
        mMenuItem.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                // 검색 버튼 눌러서 서치뷰 열렸을 떄
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true
                }

                // 서치뷰에서 뒤로가기 버튼 눌렀을 때 -> 원래의 최신순 or 인기순으로 보여준다
                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    isSearching = false
                    searchPage = 0

                    // 최신글 or 인기글 처음으로 돌아간다
                    initBookRooms()
                    return true
                }
            }
        )

        mSearchView.apply{
            this.setIconifiedByDefault(false)

            this.queryHint = String.format("책 제목을 입력해주세요")

            // 검색 submit 버튼 보이게 바꾸고, 이미지 변경
            this.isSubmitButtonEnabled = true
            val searchSubmit = this.findViewById<ImageView>(androidx.appcompat.R.id.search_go_btn)
            searchSubmit.setImageResource(R.drawable.search_25)

            // 검색 리스너, 밑에 함수 있음
            this.setOnQueryTextListener(this@HomeFragment)

            // 검색 텍스트에 뜨는 hint icon 제거
            val hintIcon = this.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            hintIcon.layoutParams = LinearLayout.LayoutParams(0, 0)
            // 텍스트 한꺼번에 지우는 close 버튼 제거
            val closeIcon = this.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeIcon.layoutParams = LinearLayout.LayoutParams(0, 0)

            // 서치뷰에서 에딧텍스트를 가져온다
            searchEditText = this.findViewById(androidx.appcompat.R.id.search_src_text)
        }
        searchEditText = mSearchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.apply{
            this.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            this.setHintTextColor(resources.getColor(R.color.colorPrimaryGray60))
            this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
        }
    }

    // 책방 추가 버튼 리스너
    private val onClickCreateRoom = View.OnClickListener {

        // 중복 클릭 방지
        if(General.isDoubledClicked()){
            return@OnClickListener
        }

        val dialog = CreateBookDialog()
        val fragmentManager = childFragmentManager
        dialog.show(fragmentManager, "create_room")
    }

    // 최신글 필터링 적용
    private val onClickNewest = View.OnClickListener {

        if(categoryFlag != ORDER_BY_NEWEST){
            initNewestBooks()
        }
        // 탭화면 닫는다
        binding.homeSortTab.visibility = View.GONE
    }

    // 최신글 0페이지부터 부르기
    private fun initNewestBooks(){

        // 플래그 변경
        categoryFlag = ORDER_BY_NEWEST
        page = 0
        hasMore = true

        // 탭 버튼 글 변경
        binding.homeSortTxt.text = String.format("최신글")

        // 최신글 불러온다
        HomeService(this).tryGetNewest(page)
    }

    // 인기글 필터링 적용
    private val onClickPopular = View.OnClickListener {

        if(categoryFlag != ORDER_BY_POPULAR){
            initPopularBooks()
        }
        // 탭화면 닫는다
        binding.homeSortTab.visibility = View.GONE

    }

    // 인기글 0페이지부터 부르기
    private fun initPopularBooks(){

        // 플래그 변경
        categoryFlag = ORDER_BY_POPULAR
        page = 0
        hasMore = true

        // 탭 버튼 글 변경
        binding.homeSortTxt.text = String.format("인기글")

        // 인기글 불러온다
        HomeService(this).tryGetPopular(page)
    }

    // 글 카테고리 선택 버튼 클릭 리스너
    private val onClickTab = View.OnClickListener {
        val homeSortTab = binding.homeSortTab
        when(homeSortTab.visibility){
            View.VISIBLE -> homeSortTab.visibility = View.GONE
            else -> homeSortTab.visibility = View.VISIBLE
        }
    }

    // 리사이클러뷰 터치 리스너 -> 바깥을 터치하면 탭이 사라지게 하고 싶은데 잘 안된다
    private val onTouchRecyclerView = View.OnTouchListener { v, event ->
        if(binding.homeSortTab.visibility == View.VISIBLE){
            binding.homeSortTab.visibility = View.GONE
            false
        }
        else{
            v.performClick()
        }
    }

    // 최신순 책방 불러오기 통신 성공
    override fun onGetNewestSuccess(response: GetNewestResponse) {
        dismissLoadingDialog()

        when(response.code){

            // 성공
            1000 -> {
                val result = response.result
                val loadedNum = result.size     // 불러온 데이터 개수

                when (page) {

                    // 새로 불러올 때
                    0 -> homeRecyclerAdapter.updateNewest(result, categoryFlag)

                    // 스크롤에 의해 불러올 때
                    else -> homeRecyclerAdapter.addNewest(result, loadedNum)
                }

                page += loadedNum
            }

            // 책방이 없음
            3000 -> hasMore = false

            // 실패
            else -> showCustomToast(
                "책방을 불러오던 중 에러가 발생했습니다, " +
                        "에러가 계속되면 관리자에게 문의해주세요."
            )
        }

        isLoading = false
    }

    // 최신순 책방 불러오기 통신 실패
    override fun onGetNewestFailure(message: String) {
        dismissLoadingDialog()
        isLoading = false

        showCustomToast(
            "책방을 불러오던 중 오류가 발생했습니다\n" +
                    "네트워크 확인 후 오류가 계속되면 관리자에게 문의해주세요"
        )
    }

    // 인기순 책방 불러오기 통신 성공
    override fun onGetPopularSuccess(response: GetPopularResponse) {
        dismissLoadingDialog()

        when(response.code){

            // 성공
            1000 -> {
                val result = response.result
                val loadedNum = result.size     // 불러온 데이터 개수

                when (page) {

                    // 새로 불러올 때
                    0 -> homeRecyclerAdapter.updatePopular(result, categoryFlag)

                    // 스크롤에 의해 불러올 때
                    else -> homeRecyclerAdapter.addPopular(result, loadedNum)
                }

                page += loadedNum
            }

            // 책방이 없음
            3000 -> hasMore = false

            //실패
            else -> showCustomToast(
                "책방을 불러오던 중 오류가 발생했습니다, " +
                        "오류가 계속되면 관리자에게 문의해주세요."
            )
        }

        isLoading = false
    }

    // 인기순 책방 불러오기 통신 실패
    override fun onGetPopularFailure(message: String) {
        dismissLoadingDialog()
        isLoading = false

        showCustomToast(
            "책방을 불러오던 중 에러가 발생했습니다, " +
                    "네트워크 확인 후 에러가 계속되면 관리자에게 문의해주세요."
        )
    }

    // 서치뷰 검색 리스너
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query?.isEmpty()!! || query == ""){
            showCustomToast("검색어를 입력해주세요")
        }else{
            isSearching = true
            isLoading = true
            searchPage = 0
            HomeService(this).tryGetSearchBooks(query.toString(), searchPage)
        }
        return true
    }

    // 책방 검색 통신 성공
    override fun onGetSearchSuccess(response: GetSearchResponse, query: String?) {
        dismissLoadingDialog()

        when(response.code){
            1000 -> {
                val result = response.result
                val loadedNum = result.size     // 불러온 데이터 개수

                General.hideKeyboard(context!!, searchEditText) // 키보드 내리기

                // 검색어로 받아오는 데이터 클래스가 NewestResult이기 때문에
                // 임시로 newest로 설정해서 데이터를 보낸다
                when (searchPage) {

                    // 새로 불러올 때
                    0 -> {
                        query?.let { bookQuery = it }  // 검색한 책 이름 저장
                        homeRecyclerAdapter.updateNewest(result, categoryFlag)
                    }

                    // 스크롤에 의해 불러올 때
                    else -> homeRecyclerAdapter.addNewest(result, loadedNum)
                }

                searchPage += loadedNum
            }

            2003 -> showCustomToast("검색어를 입력해주세요")

            3000 -> {

                // 스크롤로 검색했을 때 다음 책방이 없을 경우
                if (searchPage > 0) {
                    hasMore = false
                }

                // 첫 검색에서 검색어에 해당하는 책방이 없을 경우
                else {
                    hasMore = false
                    showCustomToast("\"${query}\"를 포함하는 책방이 존재하지 않습니다")
                }

            }

            else -> showCustomToast(
                "책방 검색 중 에러가 발생했습니다, " +
                        "에러가 계속되면 관리자에게 문의해주세요."
            )
        }

        isLoading = false
    }

    // 책방 검색 통신 실패
    override fun onGetSearchFailure(message: String) {
        dismissLoadingDialog()
        isLoading = false

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }

    // 검색 입력 변화 감지 리스너
    override fun onQueryTextChange(newText: String?): Boolean {

        /*if(isSearching){
            // 검색어를 다 지우면
            if(newText?.isEmpty()!! || newText == ""){
                isSearching = false

                // 최신글 or 인기글 처음으로 돌아간다
                initBookRooms()
            }
        }*/
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 책방을 만든 후 홈으로 돌아왔을 때 -> 새로고침
        if(requestCode == CreateBookDialog.BOOK_CREATED_CODE && resultCode == Activity.RESULT_OK){
            // 책방 목록 새로고침
            initBookRooms()
        }
        // 책방에 들어갔다 나왔는데 책방이 삭제됐을 떄
        else if(requestCode == BOOK_ENTERED_CODE && resultCode == BOOK_REMOVED){
            initBookRooms()
        }
    }

    // 서버에서 책방을 가져온다
    private fun initBookRooms(){
        showLoadingDialog(context!!)

        // 최신글 or 인기글 처음으로 돌아간다
        when(categoryFlag){
            ORDER_BY_NEWEST -> initNewestBooks()
            ORDER_BY_POPULAR -> initPopularBooks()
        }
    }

    private val onRecyclerScroll = object: RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val lastVisiblePos =    //  화면에 보이는 마지막 아이템 포지션
                (binding.homeRecycler.layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()
            val totalLastIdx = homeRecyclerAdapter.itemCount - 1    // 어댑터 아이템 마지막 인덱스

            // 어댑터에서 더 보여줄 아이템이 5개 이하로 남아있을 때
            if(!isLoading && hasMore && lastVisiblePos + 5 >= totalLastIdx){
                isLoading = true

                // 검색 중
                if(isSearching){
                    HomeService(this@HomeFragment).tryGetSearchBooks(bookQuery, searchPage)
                }

                // 일반 상황
                else {
                    when (categoryFlag) {
                        ORDER_BY_NEWEST -> HomeService(this@HomeFragment).tryGetNewest(page)
                        ORDER_BY_POPULAR -> HomeService(this@HomeFragment).tryGetPopular(page)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        searchEditText.clearFocus()
        super.onDestroyView()
    }
}