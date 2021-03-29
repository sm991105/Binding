package com.medium.binding.src.main.menu.btm_sheet

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.medium.binding.R
import com.medium.binding.databinding.LayoutBottomSheetBinding
import com.medium.binding.src.main.menu.MenuFragmentView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class BottomSheetLayout(private val menuFragmentView: MenuFragmentView): BottomSheetDialogFragment() {
    private var _binding: LayoutBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var bigLocationList: ArrayList<String> // 첫번째 지역(시/도) 리스트뷰 데이터
    private lateinit var bigAdapter: ArrayAdapter<String>   // 첫번째 지역 리스트뷰 어댑터
    lateinit var smallLocationList: ArrayList<String> // 두번째 지역(구) 리스트뷰 데이터
    lateinit var smallAdapter: ArrayAdapter<String>   // 두번째 지역 리스트뷰 어댑터
    private var selectedLocation: String? = null             // 최종 선택된 지역
    lateinit var selectedLocations: ArrayList<String> // 여러 지역 선택할 때 사용 가능
    private var bigPos = -1     // 큰 지역 pos
    private var smallPos = -1    // 작은 지역 pos

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

        // 첫번째 지역 선택 리스트뷰 설정
        bigLocationList = arrayListOf("서울", "경기도", "인천")
        bigAdapter = ArrayAdapter(context!!, R.layout.item_big_location,bigLocationList)
        binding.bottomSheetBigList.adapter = bigAdapter
        binding.bottomSheetBigList.onItemClickListener = onBigClick
        if(bigPos != -1){
            binding.bottomSheetBigList.setItemChecked(bigPos, true)     // default 값
        }


        // 두번째 지역 선택 리스트뷰 설정
        smallLocationList = arrayListOf()
        smallAdapter = ArrayAdapter(context!!, R.layout.item_small_location, smallLocationList)
        binding.bottomSheetSmallList.adapter = smallAdapter
        // 이전에 선택했던 게 있으면 그 상태로 돌아가고, 없으면 서울을 보여준다
        if(bigPos != -1){
            updateSmallListView(bigPos)
        } else{
            smallLocationList.addAll(resources.getStringArray(R.array.Seoul))
        }
        binding.bottomSheetSmallList.onItemClickListener = onSmallClick
        if(smallPos != -1){
            binding.bottomSheetSmallList.setItemChecked(smallPos, true)
        }


        selectedLocations = arrayListOf("강동구") // 여러 지역 선택할 때 사용 가능
        // 선택완료 버튼
        binding.bottomSheetButton.setOnClickListener(onClickSelect)

        // x버튼 클릭
        binding.bottomSheetCancel.setOnClickListener {
            this.dismiss()
        }
    }

    private val onClickSelect = View.OnClickListener {
        // val smallListView = binding.bottomSheetSmallList

        /*// 각 리스트 아이템이 선택되었는지, 반복문을 사용하여 확인 가능
        val checkedItems: SparseBooleanArray = smallListView.checkedItemPositions
        for(i in 0 until smallListView.count){
            if(checkedItems.get(i)){
                selectedLocations.add(smallLocationList[i])
            }
        }*/

        // Log.d("로그", "onClickSelect() called, selectedLocations: $selectedLocations")

        // menuFragmentView.changeStores(selectedLocations)

        // 지역을 선택하고 버튼을 누르면 동작
        if(selectedLocation != null){
            selectedLocations[0] = selectedLocation!!
            menuFragmentView.let{
                it.updateLocationStores(selectedLocations)
                it.updateLocationTxt(selectedLocations[0])
                it.updateLocationPos(bigPos, smallPos)
            }
        }else{
            // menuFragmentView.st
        }
        this.dismiss()
    }
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

    private fun updateSmallListView(bigPos: Int){
        when(bigPos){
            0 -> smallLocationList.let{
                it.clear()
                it.addAll(resources.getStringArray(R.array.Seoul))
            }
            1 ->
                smallLocationList.let{
                    it.clear()
                    it.addAll(resources.getStringArray(R.array.Gyeonggi))
                }
            2 -> smallLocationList.let{
                it.clear()
                it.addAll(resources.getStringArray(R.array.Incheon))
            }
        }
        smallAdapter.notifyDataSetChanged()
    }

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