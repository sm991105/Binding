package com.example.binding.src.main.menu.store_detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.binding.R
import com.example.binding.config.BaseFragment
import com.example.binding.databinding.FragmentStoreDetailBinding
import com.example.binding.src.main.menu.store_detail.models.BookStoreImages
import com.example.binding.src.main.menu.store_detail.models.GetBookStoreResponse

class StoreDetailFragment: BaseFragment<FragmentStoreDetailBinding>(
    FragmentStoreDetailBinding::bind,
    R.layout.fragment_store_detail
), StoreDetailFragmentView{

    private var bookStoreIdx = 0    // 프래그먼트 이동할 때 받은, 클릭된 현 서점의 인덱스

    private lateinit var imagesList: ArrayList<BookStoreImages> // 이미지들의 인덱스, URL이 담긴 리스트
    private var isBookMarked = 0    // 1이면 마크, 0이면 노마크

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookStoreIdx = arguments?.getInt("bookStoreIdx", 0)!!

        imagesList = ArrayList()

        showLoadingDialog(context!!)
        StoreDetailService(this).tryGetBookStore(bookStoreIdx)
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
                // 임시로 사진 넣어둠
                Glide.with(this)
                    .load(imagesList[0].imageUrl)
                    .error(R.drawable.jangu)
                    .into(binding.storeDetailCover)

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
                    binding.storeDetailTimeTxt.text = it.storeTime
                    binding.storeDetailWebAddress.text = it.siteAddress
                    binding.storeDetailPhoneTxt.text = it.phoneNumber
                    binding.storeDetailInfo.text = it.storeInfo
                }
            }
            else -> Log.d("로그", "서점 상세 조회 실패, code: ${response.code} , " +
                        "message: ${response.message}")
        }

    }

    override fun onGetBookStoreFailure(message: String) {
        Log.d("로그", "onGetBookStoreFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }
}