package com.example.binding.src.main.menu

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binding.R
import com.example.binding.config.BaseFragment
import com.example.binding.databinding.FragmentMenuBinding
import com.example.binding.src.main.menu.models.GetStoresResponse
import com.example.binding.src.main.menu.models.StoresResult

class MenuFragment: BaseFragment<FragmentMenuBinding>(
    FragmentMenuBinding::bind,
    R.layout.fragment_menu
), MenuFragmentView{

    private lateinit var storeList: ArrayList<StoresResult>
    private lateinit var menuRecyclerAdapter: MenuRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuRecyclerAdapter = MenuRecyclerViewAdapter(this)
        binding.menuRecycler.adapter = menuRecyclerAdapter
        binding.menuRecycler.layoutManager = LinearLayoutManager(activity
            , LinearLayoutManager.VERTICAL, false
        )

        // 임시로 1페이지 10개만 가져온다
        MenuService(this).tryGetAllStores(0, 10)

    }

    override fun onGetAllStoresSuccess(response: GetStoresResponse) {
        Log.d("로그", "onGetAllStoresSuccess() called, response: $response")

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

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }
}