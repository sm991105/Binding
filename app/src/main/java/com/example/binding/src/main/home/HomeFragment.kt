package com.example.binding.src.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binding.R
import com.example.binding.config.ApplicationClass
import com.example.binding.config.BaseFragment
import com.example.binding.config.BaseResponse
import com.example.binding.databinding.FragmentHomeBinding
import com.example.binding.databinding.FragmentJoin2Binding
import com.example.binding.src.main.home.models.GetNewestResponse
import com.example.binding.src.main.home.models.GetPopularResponse
import com.example.binding.src.main.home.models.NewestResult
import com.example.binding.src.main.home.models.PopularResult

class HomeFragment: BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
), HomeFragmentView{
    private val sp = ApplicationClass.sSharedPreferences

    private var newestBooksList = ArrayList<NewestResult>()
    private var popularBooksList = ArrayList<PopularResult>()
    private var categoryFlag = 0    // 0 - 최신글 / 1 - 인기글

    lateinit var homeRecyclerAdapter: HomeRecyclerViewAdapter

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
            true
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
}