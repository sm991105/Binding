package com.medium.binding.src.main.menu.store_detail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseResponse
import com.medium.binding.databinding.FragmentStoreDetailBinding
import com.medium.binding.src.main.menu.store_detail.models.BookStoreImages
import com.medium.binding.src.main.menu.store_detail.models.GetBookStoreResponse
import com.medium.binding.util.LoadingDialog


class StoreDetailFragment: Fragment(), StoreDetailFragmentView{
    private var _binding: FragmentStoreDetailBinding? = null
    private val binding get() = _binding!!
    lateinit var mLoadingDialog: LoadingDialog

    private var bookStoreIdx = 0    // 프래그먼트 이동할 때 받은, 클릭된 현 서점의 인덱스

    private lateinit var imagesList: ArrayList<BookStoreImages> // 이미지들의 인덱스, URL이 담긴 리스트
    private var webAddress: String? = null
    private var isBookMarked = 0    // 1이면 마크, 0이면 노마크

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 프래그먼트용 테마 적용
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.StoreDetailTheme)
        val mInflater = inflater.cloneInContext(contextThemeWrapper)
        _binding = FragmentStoreDetailBinding.inflate(mInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookStoreIdx = arguments?.getInt("bookStoreIdx", 0)!!

        imagesList = ArrayList()

        showLoadingDialog(context!!)
        StoreDetailService(this).tryGetBookStore(bookStoreIdx)

        // 뒤로가기 버튼
        binding

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

        // 웹 주소 클릭
        binding.storeDetailWebContainer.setOnClickListener(onClickWebAddress)

        // FrameLayout 하단 뷰로 터치 이벤트가 전달되는 것을 막는다
        binding.storeDetailRoot.setOnTouchListener { v, event ->
            v.performClick()
            true
        }
    }

    // 서점 인스타 홈페이지 띄우기 - web으로 띄우기
    private val onClickWebAddress = View.OnClickListener {

        //특정 페이지만 띄울때 사용
        val instagramPageID = String.format("$webAddress/")
        var instagramPostID = "" //게시글을 까지 보여주고싶다면 이변수를 활용
        // 만약 특정페이지 url이 있다면 게시글 url값을 초기화
        if (instagramPageID != "") instagramPostID = ""

        // val uri: Uri = Uri.parse("http://instagram.com/_u/$instagramPostID$instagramPageID")
        val uri: Uri = Uri.parse("$instagramPostID$instagramPageID")
        val instagramIntent = Intent(Intent.ACTION_VIEW, uri)

        // instagramIntent.setPackage("com.instagram.android")

        try {
            startActivity(instagramIntent)
        } catch (e: ActivityNotFoundException) {
            startActivity(instagramIntent)
        }
    }

    override fun onGetBookStoreSuccess(response: GetBookStoreResponse) {
        dismissLoadingDialog()

        when(response.code){
            1000 -> {
                val result = response.result

                val bookStoreInfo = result.bookStoreInfo[0]
                imagesList = result.images

                // 첫 사진을 대표사진으로 설정, 나머지는 순서대로 소개 내용 이후에 출력
                imagesList.forEachIndexed { idx, bookStoreImages ->
                    if(idx == 0){
                        Glide.with(this)
                            .load(bookStoreImages.imageUrl)
                            .placeholder(R.drawable.icon_app)
                            .error(R.drawable.icon_app)
                            .into(binding.storeDetailCover)
                    }

                    // 본문 사진
                    val viewId = resources.getIdentifier(
                        "store_detail_img_$idx", "id", context?.packageName)
                    val imgView: ImageView? = activity?.findViewById(viewId)
                    imgView?.let {
                        it.visibility = View.VISIBLE
                        Glide.with(this)
                            .load(bookStoreImages.imageUrl)
                            .placeholder(R.drawable.icon_app)
                            .error(R.drawable.icon_app)
                            .into(it)
                    }
                }

                // 텍스트와 겹치는 사진을 약간 어둡게 처리
                binding.storeDetailCover.setColorFilter(
                    Color.parseColor("#BDBDBD"),
                    PorterDuff.Mode.MULTIPLY
                )

                // 이미지 제외한 모든 정보 입력
                bookStoreInfo.let {
                    binding.storeDetailStoreName.text = it.storeName
                    isBookMarked = it.isBookMark
                    if (isBookMarked == 1) {
                        binding.storeDetailBookmarkFilled.visibility = View.VISIBLE
                        binding.storeDetailBookmarkEmpty.visibility = View.INVISIBLE
                    } else {
                        binding.storeDetailBookmarkFilled.visibility = View.INVISIBLE
                        binding.storeDetailBookmarkEmpty.visibility = View.VISIBLE
                    }
                    binding.storeDetailLocationTxt.text = it.location

                    this.webAddress = it.siteAddress
                    // 인스타 주소 -> "@계정"으로 변환해서 출력
                    it.siteAddress.apply{
                        val startIdx = this.indexOf("/",9) + 1
                        val tempIdx = this.indexOf("/", startIdx)
                        val lastIdx: Int = if(tempIdx >= startIdx){
                            tempIdx-1
                        }else{
                            this.lastIndex
                        }
                        binding.storeDetailWebAddress.text =
                            String.format("@${this.substring(startIdx..lastIdx)}")
                    }

                    binding.storeDetailPhoneTxt.text = it.phoneNumber
                    if(it.storeInfo != "-1"){
                        binding.storeDetailInfo.text = it.storeInfo
                    }
                }
            }
            else -> showCustomToast("서점 정보를 가져오던 중 에러가 발생했습니다, 에러가 계속되면" +
                    "관리자에게 문의해주세요")
        }

    }

    override fun onGetBookStoreFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("서점 정보를 가져오던 중 에러가 발생했습니다, 에러가 계속되면" +
                "관리자에게 문의해주세요")
    }

    // 북마크 수정 API 콜백
    override fun onPatchBookmarkSuccess(response: BaseResponse) {
        dismissLoadingDialog()

        when(response.code){

            // 북마크 첫 추가, 북마크 ON
            in 1000..1001 -> {
                isBookMarked = 1
                binding.storeDetailBookmarkFilled.visibility = View.VISIBLE
                binding.storeDetailBookmarkEmpty.visibility = View.INVISIBLE
                ApplicationClass.isMarkEdited = true
            }
            1002 -> {
                isBookMarked = 0
                binding.storeDetailBookmarkFilled.visibility = View.INVISIBLE
                binding.storeDetailBookmarkEmpty.visibility = View.VISIBLE
                ApplicationClass.isMarkEdited = true
            }
            else -> showCustomToast("북마크 수정 중 에러가 발생했습니다, 에러가 계속되면" +
                    "관리자에게 문의해주세요")
        }
    }

    override fun onPatchBookmarkFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("북마크 수정 중 에러가 발생했습니다, 에러가 계속되면" +
                "관리자에게 문의해주세요")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showCustomToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingDialog(context: Context) {
        mLoadingDialog = LoadingDialog(context)
        mLoadingDialog.show()
    }

    private fun dismissLoadingDialog() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
    }
}