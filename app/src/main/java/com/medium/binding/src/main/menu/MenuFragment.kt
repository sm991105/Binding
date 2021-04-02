package com.medium.binding.src.main.menu

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseFragment
import com.medium.binding.databinding.FragmentMenuBinding
import com.medium.binding.src.main.menu.btm_sheet.BottomSheetLayout
import com.medium.binding.src.main.menu.models.GetStoresResponse
import com.medium.binding.src.main.menu.models.StoresResult

class MenuFragment: BaseFragment<FragmentMenuBinding>(
    FragmentMenuBinding::bind,
    R.layout.fragment_menu
), MenuFragmentView{

    companion object{
        var bigPos: Int = 0
        var smallPos: Int = 0
        var selectedLocation: String? = null
    }

    var hasNext = true
    var page: Int = 0
    val limit = 200
    private lateinit var menuRecyclerAdapter: MenuRecyclerViewAdapter

    private var selectedLocationList = ArrayList<String>()   // API 호출에 사용할 선택 지역 리스트

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 마지막으로 선택한 지역
        binding.menuLocation.text = selectedLocation ?: "전체"

        menuRecyclerAdapter = MenuRecyclerViewAdapter(this)
        binding.menuRecycler.adapter = menuRecyclerAdapter
        binding.menuRecycler.layoutManager = LinearLayoutManager(activity
            , LinearLayoutManager.VERTICAL, false
        )

        // 화면 진입했을 때 보일 서점들 설정
        initStores()

        // 지역 선택 버튼 클릭
        binding.menuTopBar.setOnClickListener(onClickLocation)

        // binding.menuRecycler.addOnScrollListener(onRecyclerScroll)

    }

    /*private val onRecyclerScroll = object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }*/

    // 처음 화면 진입했을 떄 보여줄 서점들 설정
    private fun initStores(){

        // 전체 - 전체
        if(bigPos == 0 && smallPos == 0){
            // 서점 100몇개 다 가져옴
            showLoadingDialog(context!!)
            MenuService(this).tryGetAllStores(0, limit)
        }

        // 특정 지역 - 전체
        else if(smallPos == 0){
            when(bigPos){
                1 -> {
                    selectedLocationList.clear()
                    selectedLocationList.addAll(resources.getStringArray(R.array.Seoul))
                }
                2 -> {
                    selectedLocationList.clear()
                    selectedLocationList.addAll(resources.getStringArray(R.array.Gyeonggi))
                }
                3 -> {
                    selectedLocationList.clear()
                    selectedLocationList.addAll(resources.getStringArray(R.array.Incheon))
                }
            }
            updateLocationStores(selectedLocationList)
        }

        // 지역 - 지역
        else{
            updateLocationStores(arrayListOf(selectedLocation!!))
        }
    }

    // 지역 선택 버튼 클릭 -> Bottom Sheet 보여준다
    private val onClickLocation = View.OnClickListener {
        val btmSheet = BottomSheetLayout(this)

        val fragmentManager = childFragmentManager
        btmSheet.show(fragmentManager, "Location")
    }

    override fun onGetAllStoresSuccess(response: GetStoresResponse) {
        dismissLoadingDialog()

        when(response.code){
            // 조회에 성공하면 리사이클러뷰에 서점 데이터를 전달한다
            1000 -> {
                val result = response.result

                // 서점 데이터 전달
                menuRecyclerAdapter.updateList(result)
            }

            else -> {
                showCustomToast("서점정보를 가져오지 못했습니다, 에러가 계속되면 관리자에게 문의해주세요")
            }
        }
    }

    override fun onGetAllStoresFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("서점정보를 가져오지 못했습니다, 네트워크 확인 후 에러가 계속되면 관리자에게 문의해주세요")
    }

    override fun onGetLocationStoresSuccess(response: GetStoresResponse) {
        dismissLoadingDialog()

        when(response.code){
            // 조회에 성공하면 리사이클러뷰에 서점 데이터를 전달한다
            1000 -> {
                val result = response.result

                if(result.size <= 0){
                    showCustomToast("해당 지역 서점을 제보해주세요")
                }
                menuRecyclerAdapter.updateList(result)  // 서점 데이터 전달
            }

            else -> {
                response.message?.let{showCustomToast(it)}
            }
        }
    }

    override fun onGetLocationStoresFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("서점정보를 가져오지 못했습니다, 네트워크 확인 후 에러가 계속되면 관리자에게 문의해주세요")
    }

    // 바텀시트에서 선택 -> 지역 서점 가져오기
    override fun updateLocationStores(LocationList: ArrayList<String>) {
        showLoadingDialog(context!!)
        MenuService(this).tryGetLocationStores(page, limit, LocationList)
    }

    // 바텀시트에서 선택 -> 전체 서점 가져오기
    override fun getAllStores() {
        showLoadingDialog(context!!)
        MenuService(this).tryGetAllStores(page, limit)
    }

    // 선택한 지역으로 TEXT 값 변경
    override fun updateLocationTxt(location: String) {
        if(location == "전체"){
            binding.menuLocation.text = resources.getStringArray(R.array.big_location)[bigPos]
        }else{
            binding.menuLocation.text = location
        }

        selectedLocation = location
    }

    // 선택한 BottomSheetLayout의 리스트뷰의 pos 값으로 업데이트
    override fun updateLocationPos(bigPosition: Int, smallPosition: Int) {
        bigPos = bigPosition
        smallPos = smallPosition
    }
}