package com.makeus6.binding.src.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.makeus6.binding.R
import com.makeus6.binding.config.ApplicationClass
import com.makeus6.binding.config.BaseFragment
import com.makeus6.binding.databinding.FragmentHomeBinding
import com.makeus6.binding.src.main.home.create_room.CreateBookDialog
import com.makeus6.binding.src.main.home.models.*
import java.util.*

class HomeFragment: BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
), HomeFragmentView, SearchView.OnQueryTextListener{
    private val sp = ApplicationClass.sSharedPreferences

    private var newestBooksList = ArrayList<NewestResult>()
    private var popularBooksList = ArrayList<PopularResult>()
    private var categoryFlag = 0    // 0 - 최신글 / 1 - 인기글

    lateinit var homeRecyclerAdapter: HomeRecyclerViewAdapter

    private lateinit var mSearchView: SearchView
    private lateinit var searchEditText: EditText

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 최근 적용한 필터가 인기글인지 최신글인지 확인
        categoryFlag = sp.getInt("homeCategory", 0)

        // 책방 리사이클러 뷰 초기화
        homeRecyclerAdapter = HomeRecyclerViewAdapter(this,
            newestBooksList, popularBooksList, categoryFlag
        )
        binding.homeRecycler.adapter = homeRecyclerAdapter
        binding.homeRecycler.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        // 책방 불러오기
        showLoadingDialog(context!!)
        when(binding.homeSortTxt.text.toString()){
            "최신글" -> HomeService(this).tryGetNewest()
            "인기글" -> HomeService(this).tryGetPopular()
        }

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
        mSearchView = toolbar.menu.findItem(R.id.menu_home_toolbar_search).actionView as SearchView

        // 서치뷰에서 뒤로가기 버튼 눌렀을 때 동작 정의하기 위함
        toolbar.menu.findItem(R.id.menu_home_toolbar_search).setOnActionExpandListener(
            object: MenuItem.OnActionExpandListener {
                    // 검색 버튼 눌러서 서치뷰 열렸을 떄
                    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    Log.d("로그", "onMenuItemActionExpand() called")
                    return true
                }

                // 서치뷰에서 뒤로가기 버튼 눌렀을 때 -> 원래의 최신순 or 인기순으로 보여준다
                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    Log.d("로그", "onMenuItemActionCollapse() called")
                    when(categoryFlag){
                        0 -> HomeService(this@HomeFragment).tryGetNewest()
                        1 -> HomeService(this@HomeFragment).tryGetPopular()
                    }
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

            this.setOnQueryTextFocusChangeListener { v, hasExpanded ->
                when(hasExpanded){
                    true -> {}
                    false -> {}
                }
            }

            // 검색 텍스트에 뜨는 hint icon 제거
            val hintIcon = this.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            hintIcon.layoutParams = LinearLayout.LayoutParams(0,0)
            // 텍스트 한꺼번에 지우는 close 버튼 제거
            val closeIcon = this.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeIcon.layoutParams = LinearLayout.LayoutParams(0,0)

            // 서치뷰에서 에딧텍스트를 가져온다
            searchEditText = this.findViewById(androidx.appcompat.R.id.search_src_text)
        }
        searchEditText = mSearchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.apply{
            this.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            this.setHintTextColor(resources.getColor(R.color.colorPrimaryGray60))
            this.setTextSize(TypedValue.COMPLEX_UNIT_SP ,15F)
        }
    }

    // 책방 추가 버튼 리스너
    private val onClickCreateRoom = View.OnClickListener {
        val dialog = CreateBookDialog(context!!)
        val fragmentManager = childFragmentManager
        dialog.show(fragmentManager, "create_room")
    }

    // 최신글 필터링 적용
    private val onClickNewest = View.OnClickListener {

        if(categoryFlag != 0){
            // 탭 버튼 글 변경
            binding.homeSortTxt.text = String.format("최신글")

            // 최신글 불러온다 -> sp에 categoryFlag 저장 포함
            HomeService(this).tryGetNewest()
        }
        // 탭화면 닫는다
        binding.homeSortTab.visibility = View.GONE
    }

    // 인기글 필터링 적용
    private val onClickPopular = View.OnClickListener {

        if(categoryFlag != 1){
            // 탭 버튼 글 변경
            binding.homeSortTxt.text = String.format("인기글")

            // 인기글 불러온다 -> sp에 categoryFlag 저장 포함
            HomeService(this).tryGetPopular()
        }
        // 탭화면 닫는다
        binding.homeSortTab.visibility = View.GONE

    }

    // 글 카테고리 선택 버튼 클릭 리스너
    private val onClickTab = View.OnClickListener {
        val homeSortTab = binding.homeSortTab
        when(homeSortTab.visibility){
            View.VISIBLE -> homeSortTab.visibility = View.GONE
            else -> homeSortTab.visibility = View.VISIBLE
        }
    }

    // 리사이클러뷰 터치 리스너
    private val onTouchRecyclerView = View.OnTouchListener { v, event ->
        if(binding.homeSortTab.visibility == View.VISIBLE){
            binding.homeSortTab.visibility = View.GONE
        }
        v.performClick()
    }

    // 최신순 책방 불러오기 통신 성공
    override fun onGetNewestSuccess(response: GetNewestResponse) {
        Log.d("로그", "onGetNewestSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){

            // 성공
            1000 -> {
                Log.d("로그", "최신순 책방 불러오기 성공")

                categoryFlag = 0
                homeRecyclerAdapter.updateNewest(response.result, categoryFlag)
                sp.edit().putInt("homeCategory", categoryFlag).apply()
            }

            // 책방이 없음
            4000 -> Log.d("로그", "서버에 최신순 책방 데이터가 없음")

            //실패
            else -> Log.d("로그", "최신순 책방 불러오기 실패, message: ${response.message}")
        }
    }

    // 최신순 책방 불러오기 통신 실패
    override fun onGetNewestFailure(message: String) {
        Log.d("로그", "onGetNewestFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }

    // 인기순 책방 불러오기 통신 성공
    override fun onGetPopularSuccess(response: GetPopularResponse) {
        Log.d("로그", "onGetPopularSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){

            // 성공
            1000 -> {
                Log.d("로그", "인기순 책방 불러오기 성공")

                categoryFlag = 1
                homeRecyclerAdapter.updatePopular(response.result, categoryFlag)
                sp.edit().putInt("homeCategory", categoryFlag).apply()
            }

            // 책방이 없음
            4000 -> Log.d("로그", "서버에 최신순 책방 데이터가 없음")

            //실패
            else -> Log.d("로그", "최신순 책방 불러오기 실패, message: ${response.message}")
        }

    }

    // 인기순 책방 불러오기 통신 실패
    override fun onGetPopularFailure(message: String) {
        Log.d("로그", "onGetPopularFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }

    // 책방 검색 통신 성공
    override fun onGetSearchSuccess(response: GetSearchResponse, query: String?) {
        Log.d("로그", "onGetSearchSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){
            1000 -> {
                Log.d("로그", "책방 검색 성공, result: ${response.result}")

                // 검색어로 받아오는 데이터 클래스가 NewestResult이기 때문에
                // 임시로 newest로 설정해서 데이터를 보낸다
                homeRecyclerAdapter.updateNewest(response.result, 0)
            }

            2003 -> showCustomToast("검색어를 입력해주세요")

            3000 -> {
                showCustomToast("\"${query}\"를 포함하는 책방이 존재하지 않습니다")
            }

            else -> Log.d("로그", "책방 검색 실패, message: ${response.message}")
        }
    }

    // 책방 검색 통신 실패
    override fun onGetSearchFailure(message: String) {
        Log.d("로그", "onGetSearchFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }

    // 서치뷰 검색 리스너
    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d("로그", "검색 = query: $query")

        if(query?.isEmpty()!! || query == ""){
            showCustomToast("검색어를 입력해주세요")
        }else{
            HomeService(this).tryGetSearchBooks(query.toString())
        }
        return true
    }

    // 검색 입력 변화 감지 리스너
    override fun onQueryTextChange(newText: String?): Boolean {

        // 검색어를 다 지우면 원래 화면으로 돌아간다
        if(newText?.isEmpty()!! || newText == ""){
            when(categoryFlag){
                0 -> HomeService(this@HomeFragment).tryGetNewest()
                1 -> HomeService(this@HomeFragment).tryGetPopular()
            }
        }
        return true
    }
}