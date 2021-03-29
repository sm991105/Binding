package com.medium.binding.src.main.menu.btm_sheet

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.medium.binding.R
import com.medium.binding.databinding.LayoutBottomSheetBinding
import com.medium.binding.src.main.menu.MenuFragmentView
import java.util.*


class BottomSheetLayout(private val menuFragmentView: MenuFragmentView): BottomSheetDialogFragment() {
    private var _binding: LayoutBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var listWhole: ArrayList<String> = ArrayList()
    private var listSeoul: ArrayList<String> = ArrayList()
    private var listGyeonggi: ArrayList<String> = ArrayList()
    private var listIncheon: ArrayList<String> = ArrayList()

    private lateinit var bigLocationList: ArrayList<String> // 첫번째 지역(시/도) 리스트뷰 데이터
    private lateinit var bigAdapter: ArrayAdapter<String>   // 첫번째 지역 리스트뷰 어댑터
    lateinit var smallLocationList: ArrayList<String> // 두번째 지역(구) 리스트뷰 데이터
    lateinit var smallAdapter: ArrayAdapter<String>   // 두번째 지역 리스트뷰 어댑터
    private var selectedLocation: String? = null             // 최종 선택된 지역
    private var selectedLocationList = ArrayList<String>() // 여러 지역 선택할 때 사용 가능
    private var bigPos = 0    // 큰 지역 pos
    private var smallPos = 0   // 작은 지역 pos


    // 크기 설정
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val btmSheetDialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        // 커스텀 다이얼로그 뷰 생성,적용
        val view = View.inflate(context, R.layout.layout_bottom_sheet, null)
        btmSheetDialog.setContentView(view)

        // 바텀시트 크기 설정
        val btmSheetContainer = btmSheetDialog.findViewById<View>(R.id.design_bottom_sheet)
        val params = btmSheetContainer?.layoutParams
        val screenHeight = activity!!.resources.displayMetrics.heightPixels
        params?.height = (screenHeight * 0.66).toInt()
        // params?.height = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        btmSheetContainer?.layoutParams = params

        return btmSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // default 선택 지역 위한 pos 값들
        arguments?.let {
            bigPos = it.getInt("bigPos")
            smallPos = it.getInt("smallPos")
        }

        // 지역 smallList들 미리 설정
        listSeoul.addAll(resources.getStringArray(R.array.Seoul))
        listGyeonggi.addAll(resources.getStringArray(R.array.Gyeonggi))
        listIncheon.addAll(resources.getStringArray(R.array.Incheon))
        listWhole.apply{
            this.addAll(listSeoul)
            this.addAll(listGyeonggi.subList(1, listGyeonggi.lastIndex))
            this.addAll(listIncheon.subList(1, listIncheon.lastIndex))
        }
        Log.d("로그", "서울 리스트: $listSeoul , 전체 리스트: $listWhole")

        // 첫번째 지역 선택 리스트뷰 설정
        bigLocationList = arrayListOf("전체", "서울", "경기도", "인천")
        bigAdapter = ArrayAdapter(context!!, R.layout.item_big_location, bigLocationList)
        binding.bottomSheetBigList.adapter = bigAdapter
        binding.bottomSheetBigList.onItemClickListener = onBigClick
        if(bigPos != -1){
            binding.bottomSheetBigList.setItemChecked(bigPos, true)     // default 값
            binding.bottomSheetBigList.setSelection(bigPos)
        }


        // 두번째 지역 선택 리스트뷰 설정
        smallLocationList = arrayListOf()
        smallAdapter = ArrayAdapter(context!!, R.layout.item_small_location, smallLocationList)
        binding.bottomSheetSmallList.adapter = smallAdapter
        // 이전에 선택했던 게 있으면 그 상태로 돌아가고, 없으면 전체를 보여준다
        if(bigPos != -1){
            updateSmallListView(bigPos)
        } else{
            smallLocationList.addAll(listWhole)
        }
        binding.bottomSheetSmallList.onItemClickListener = onSmallClick
        if(smallPos != -1){
            binding.bottomSheetSmallList.setItemChecked(smallPos, true)
            binding.bottomSheetSmallList.setSelection(smallPos)
        }

        // 선택완료 버튼
        binding.bottomSheetButton.setOnClickListener(onClickSelect)

        // x버튼 클릭
        binding.bottomSheetCancel.setOnClickListener {
            this.dismiss()
        }
    }

    // 선택 완료 버튼
    private val onClickSelect = View.OnClickListener {
        val locationTxt: String?

        // 전지역 전체 서점 선택
        if(bigPos == 0 && smallPos == 0){
            menuFragmentView.let{
                it.getAllStores()
                it.updateLocationPos(bigPos, smallPos)
                it.updateLocationTxt("전체")
            }
        }

        // 특정 지역 서점 선택
        else{
            // bigLocation의 전체 서점
            if(smallPos == 0){
                selectedLocationList.addAll(smallLocationList)
                locationTxt = bigLocationList[bigPos]
                selectedLocationList.removeAt(0)  // selectedLocationList[0] ("전체") 삭제
            }
            // bigLocation의 특정 지역 서점
            else {
                selectedLocationList.add(smallLocationList[smallPos])
                locationTxt = smallLocationList[smallPos]
            }

            // 반영
            menuFragmentView.let{
                it.updateLocationStores(selectedLocationList)
                it.updateLocationPos(bigPos, smallPos)
                it.updateLocationTxt(locationTxt)
            }
        }

        this.dismiss()
    }

    // 왼쪽 리스트뷰 아이템을 선택했을때
    private val onBigClick = AdapterView.OnItemClickListener { parent, view, position, id ->
        val location = (view as TextView).text.toString()
        Log.d("로그", "location1: $location")

        if(view.isActivated){
            bigPos = position
        }else{
            bigPos = -1
        }
        // 선택한 지역에 맞게 2번째 리스트뷰 업데이트
        updateSmallListView(position)
    }

    // 오른쪽 리스트뷰 아이템을 선택했을 때
    private val onSmallClick = AdapterView.OnItemClickListener { parent, view, position, id ->
        val location = (view as TextView).text.toString()
        Log.d("로그", "location2: $location")

        if(view.isActivated) {
            selectedLocation = location
            smallPos = position
        }else{
            selectedLocation = null
            smallPos = -1
        }
    }

    // 왼쪽 리스트뷰 아이템 선택에 따라 오른쪽 리스트뷰의 배열을 바꾼다
    private fun updateSmallListView(bigPos: Int){
        when(bigPos){
            0 -> smallLocationList.apply {
                this.clear()
                this.addAll(listWhole)
            }
            1 -> smallLocationList.apply {
                this.clear()
                this.addAll(listSeoul)
            }
            2 -> smallLocationList.apply {
                this.clear()
                this.addAll(listGyeonggi)
            }
            3 -> smallLocationList.apply {
                this.clear()
                this.addAll(listIncheon)
            }
        }
        smallAdapter.notifyDataSetChanged()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showCustomToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}