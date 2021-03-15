package com.example.binding.src.main.menu.store_detail

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.binding.R
import com.example.binding.config.ApplicationClass
import com.example.binding.config.BaseFragment
import com.example.binding.config.BaseResponse
import com.example.binding.databinding.FragmentStoreDetailBinding
import com.example.binding.src.main.menu.store_detail.models.BookStoreImages
import com.example.binding.src.main.menu.store_detail.models.GetBookStoreResponse

class StoreDetailFragment: BaseFragment<FragmentStoreDetailBinding>(
    FragmentStoreDetailBinding::bind,
    R.layout.fragment_store_detail
), StoreDetailFragmentView{

    private val sp = ApplicationClass.sSharedPreferences
    private var bookStoreIdx = 0    // 프래그먼트 이동할 때 받은, 클릭된 현 서점의 인덱스

    private lateinit var imagesList: ArrayList<BookStoreImages> // 이미지들의 인덱스, URL이 담긴 리스트
    private var isBookMarked = 0    // 1이면 마크, 0이면 노마크


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 상태바 영역까지 확장
        val mWindow = activity?.window
        mWindow?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        bookStoreIdx = arguments?.getInt("bookStoreIdx", 0)!!

        imagesList = ArrayList()

        showLoadingDialog(context!!)
        StoreDetailService(this).tryGetBookStore(bookStoreIdx)

        // 북마크 ON
        binding.storeDetailBookmarkEmpty.setOnClickListener {
            showLoadingDialog(context!!)

            // 북마크 수정 API 호출
            StoreDetailService(this).tryPatchBookmark(bookStoreIdx)
        }

        // 북마크 해제
        binding.storeDetailBookmarkFilled.setOnClickListener {
            showLoadingDialog(context!!)

            // 북마크 수정 API 호출
            StoreDetailService(this).tryPatchBookmark(bookStoreIdx)
        }
    }


    override fun onGetBookStoreSuccess(response: GetBookStoreResponse) {
        Log.d("로그", "onGetBookStoreSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){
            1000 -> {
                Log.d("로그", "서점 상세 조회 성공, code: ${response.code} , " +
                        "message: ${response.message}")

                val result = response.result

                val bookStoreInfo = result.bookStoreInfo[0]
                imagesList = result.images

                // 첫 사진을 대표사진으로 설정
                Glide.with(this)
                    .load(imagesList[0].imageUrl)
                    .placeholder(R.drawable.icon_app)
                    .error(R.drawable.icon_app)
                    .into(binding.storeDetailCover)

                // 텍스트와 겹치는 사진을 약간 어둡게 처리
                binding.storeDetailCover.setColorFilter(Color.parseColor("#BDBDBD"),
                PorterDuff.Mode.MULTIPLY)

                // 이미지 제외한 모든 정보 입력
                bookStoreInfo.let{
                    binding.storeDetailStoreName.text = it.storeName
                    isBookMarked = it.isBookMark
                    if(isBookMarked == 1){
                        binding.storeDetailBookmarkFilled.visibility = View.VISIBLE
                        binding.storeDetailBookmarkEmpty.visibility = View.INVISIBLE
                    }else{
                        binding.storeDetailBookmarkFilled.visibility = View.INVISIBLE
                        binding.storeDetailBookmarkEmpty.visibility = View.VISIBLE
                    }
                    binding.storeDetailLocationTxt.text = it.location
                    binding.storeDetailWebAddress.text = it.siteAddress
                    binding.storeDetailPhoneTxt.text = it.phoneNumber
                    binding.storeDetailInfo.text = it.storeInfo
                }
            }
            else -> Log.d("로그", "서점 상세 조회 실패, code: ${response.code} , " +
                        "message: ${response.message}")
        }

    }

    // 북마크 수정 API 콜백

    override fun onGetBookStoreFailure(message: String) {
        Log.d("로그", "onGetBookStoreFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }


    override fun onPatchBookmarkSuccess(response: BaseResponse) {
        Log.d("로그", "onPatchBookmarkSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){
            in 1000..1001 -> {
                isBookMarked = 1
                binding.storeDetailBookmarkFilled.visibility = View.VISIBLE
            }
            1002 -> {
                isBookMarked = 0
                binding.storeDetailBookmarkFilled.visibility = View.GONE
            }
            else -> Log.d("로그", "북마크 수정 실패, message: ${response.message}")
        }
    }

    override fun onPatchBookmarkFailure(message: String) {
        Log.d("로그", "onPatchBookmarkFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }
}