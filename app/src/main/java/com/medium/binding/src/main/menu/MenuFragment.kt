package com.medium.binding.src.main.menu

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
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

    private val sp = ApplicationClass.sSharedPreferences

    private lateinit var storeList: ArrayList<StoresResult>
    private lateinit var menuRecyclerAdapter: MenuRecyclerViewAdapter
    private var bigPos = -1      // 큰 지역 리스트뷰 선택된  pos
    private var smallPos = -1    // 작은 지역 리스트뷰 선택된  pos
    private var selectedLoction: String? = null   // 선택된 지역

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이전에 선택했던 지역 pos 값, text 값
        sp.let{
            bigPos = it.getInt("bigPos", -1)
            smallPos = it.getInt("smallPos", -1)
            selectedLoction = it.getString("selectedLocation", null)
            if(selectedLoction != null){
                binding.menuLocation.text = selectedLoction
            }

        }

        menuRecyclerAdapter = MenuRecyclerViewAdapter(this)
        binding.menuRecycler.adapter = menuRecyclerAdapter
        binding.menuRecycler.layoutManager = LinearLayoutManager(activity
            , LinearLayoutManager.VERTICAL, false
        )

        if(smallPos == -1){
            // 임시로 1페이지 30개만 가져온다, 전체 서점
            showLoadingDialog(context!!)
            MenuService(this).tryGetAllStores(0, 30)
        }else{
            updateLocationStores(arrayListOf(selectedLoction!!))
        }

        // 지역 선택 버튼 클릭
        binding.menuLocation.setOnClickListener(onClickLocation)

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

    // 지역 선택 버튼 클릭 -> Bottom Sheet 보여준다
    private val onClickLocation = View.OnClickListener {
        val btmSheet = BottomSheetLayout(this)

        // 지금 선택되어있는 지역 pos 값 전달
        val posBundle = Bundle()
        posBundle.putInt("bigPos", bigPos)
        posBundle.putInt("smallPos", smallPos)
        btmSheet.arguments = posBundle

        val fragmentManager = childFragmentManager
        btmSheet.show(fragmentManager, "Location")
    }

    override fun onGetAllStoresSuccess(response: GetStoresResponse) {
        Log.d("로그", "onGetAllStoresSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){
            // 조회에 성공하면 리사이클러뷰에 서점 데이터를 전달한다
            1000 -> {
                val result = response.result
                Log.d("로그", "전체 서점 조회 성공 - result: $result")

                storeList = ArrayList()
                storeList = result

                // 서점 데이터 전달
                menuRecyclerAdapter.updateList(storeList)
            }

            else -> {
                Log.d("로그", "전체 서점 조회 실패 - message: ${response.message}")

                response.message?.let{showCustomToast(it)}
            }
        }
    }

    override fun onGetAllStoresFailure(message: String) {
        Log.d("로그", "onGetAllStoresFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }

    override fun onGetLocationStoresSuccess(response: GetStoresResponse) {
        Log.d("로그", "onGetLocationStoresSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){
            // 조회에 성공하면 리사이클러뷰에 서점 데이터를 전달한다
            1000 -> {
                val result = response.result
                Log.d("로그", "지역 서점 조회 성공 - result: $result")

                storeList = ArrayList()
                storeList = result

                // 서점 데이터 전달
                menuRecyclerAdapter.updateList(storeList)

            }

            else -> {
                Log.d("로그", "지역 서점 조회 실패 - message: ${response.message}")

                response.message?.let{showCustomToast(it)}
            }
        }
    }

    override fun onGetLocationStoresFailure(message: String) {
        Log.d("로그", "onGetLocationStoresFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }

    // 바텀시트에서 선택 -> 지역 서점 가져오기
    override fun updateLocationStores(LocationList: ArrayList<String>) {
        showLoadingDialog(context!!)
        MenuService(this).tryGetLocationStores(0, 15, LocationList)
    }

    // 바텀시트에서 선택 -> 전체 서점 가져오기
    override fun getAllStores() {
        showLoadingDialog(context!!)
        MenuService(this).tryGetAllStores(0, 15)
    }

    // 선택한 지역으로 TEXT 값 변경
    override fun updateLocationTxt(location: String) {
        binding.menuLocation.text = location
        selectedLoction = location
        sp.edit().putString("selectedLocation", selectedLoction).apply()
    }

    // 선택한 BottomSheetLayout의 리스트뷰의 pos 값으로 업데이트
    override fun updateLocationPos(bigPosition: Int, smallPosition: Int) {
        bigPos = bigPosition
        smallPos = smallPosition
        sp.edit().let{
            it.putInt("bigPos", bigPos)
            it.putInt("smallPos", smallPos)
        }.apply()
    }
}