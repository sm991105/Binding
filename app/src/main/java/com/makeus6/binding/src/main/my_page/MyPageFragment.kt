package com.makeus6.binding.src.main.my_page

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.makeus6.binding.R
import com.makeus6.binding.config.BaseFragment
import com.makeus6.binding.databinding.FragmentMyPageBinding
import com.makeus6.binding.src.main.my_page.models.*
import com.makeus6.binding.src.main.my_page.settings.SettingsFragment
import java.util.*
import kotlin.collections.ArrayList

class MyPageFragment: BaseFragment<FragmentMyPageBinding>(
    FragmentMyPageBinding::bind,
    R.layout.fragment_my_page
), MyPageFragmentView{

    lateinit var sBookMarkAdapter: SBookMarkRecyclerAdapter
    lateinit var wBookMarkAdapter: WBookMarkRecyclerAdapter
    lateinit var writingAdapter: WritingRecyclerAdapter

    private var userImgUrl: String = "-1"

    lateinit var fontKr: Typeface
    lateinit var fontBold: Typeface

    private var writingFlag = 0     // 0 - 내가 쓴 글 , 1 - 북마크 글

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fontKr = ResourcesCompat.getFont(context!!, R.font.notosanskrregular)!!
        fontBold = ResourcesCompat.getFont(context!!, R.font.notosanskrbold)!!

        // 북마크한 서점 리사이클러뷰
        sBookMarkAdapter = SBookMarkRecyclerAdapter(this)
        binding.myPageRecyclerStore.apply {
            this.adapter = sBookMarkAdapter
            this.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false
            )
        }

        // 내가 쓴 글 리사이클러 뷰
        writingAdapter = WritingRecyclerAdapter(this)
        binding.myPageRecyclerMine.apply{
            this.adapter = writingAdapter
            this.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false
            )
        }

        // 북마크한 글 리사이클러 뷰
        wBookMarkAdapter = WBookMarkRecyclerAdapter(this)
        binding.myPageRecyclerBookmark.apply{
            this.adapter = wBookMarkAdapter
            this.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false
            )
        }

        // 유저 정보를 불러온다
        showLoadingDialog(context!!)
        MyPageService(this).tryGetUser()

        // 내가 쓴 글 버튼 클릭
        binding.myPagePostMine.setOnClickListener{
            if(writingFlag != 0){
                writingFlag = 0

                // 텍스트 위에 점 교체
                binding.myPagePostDotMine.visibility = View.VISIBLE
                binding.myPagePostDotBookmark.visibility = View.INVISIBLE

                // 텍스트 폰트 변경
                binding.myPagePostMine.typeface = fontBold
                binding.myPagePostBookmark.typeface = fontKr

                // 리사이클러 뷰 교체
                binding.myPageRecyclerMine.visibility = View.VISIBLE
                binding.myPageRecyclerBookmark.visibility = View.INVISIBLE
            }
        }

        // 북마크한 글 버튼 클릭
        binding.myPagePostBookmark.setOnClickListener{
            if(writingFlag != 1){
                writingFlag = 1

                // 글씨 위에 점 교체
                binding.myPagePostDotBookmark.visibility = View.VISIBLE
                binding.myPagePostDotMine.visibility = View.INVISIBLE

                // 텍스트 폰트 변경
                binding.myPagePostMine.typeface = fontKr
                binding.myPagePostBookmark.typeface = fontBold

                // 리사이클러 뷰 교체
                binding.myPageRecyclerMine.visibility = View.INVISIBLE
                binding.myPageRecyclerBookmark.visibility = View.VISIBLE
            }
        }

        binding.myPageBtnSettings.setOnClickListener(onClickSettings)
    }

    // 설정 버튼 클릭
    private val onClickSettings = View.OnClickListener {
        val settingsFragment = SettingsFragment(this)
        val cFragmentManager = childFragmentManager
        binding.myPageFrm.visibility = View.VISIBLE
        cFragmentManager.beginTransaction().apply{
            this.add(R.id.my_page_frm, settingsFragment)
                .commitAllowingStateLoss()
            this.addToBackStack("settings")
        }
    }

    override fun onGetUserSuccess(response: GetUserResponse) {
        Log.d("로그", "onGetUserSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){

            // 유저 정보 불러오기 성공
            1000 -> {
                val result = response.result
                Log.d("로그", "유저 정보 불러오기 성공 - result: $result")

                // 유저 정보
                val userInfo: ArrayList<InfoData> = result.info
                // 유저가 쓴 글 리스트
                val tempWritingList: ArrayList<ArrayList<WritingData>> = result.writing
                lateinit var writingList: ArrayList<WritingData>
                // 유저가 북마크한 글 리스트
                val tempWBookMarkList: ArrayList<ArrayList<WBookMarkData>> = result.writingBookMark
                lateinit var wBookMarkList: ArrayList<WBookMarkData>
                // 유저가 북마크한 서점 리스트
                val tempSBookMarkList: ArrayList<ArrayList<SBookMarkData>> =
                    result.bookstoreBookMark
                lateinit var sBookMarkList: ArrayList<SBookMarkData>

                // 유저 정보
                // 프로필 사진, 닉네임, 이메일
                userInfo.get(0).apply {
                    this@MyPageFragment.userImgUrl = this.userImgUrl

                    Glide.with(this@MyPageFragment)
                        .load(this.userImgUrl)
                        .error(R.drawable.icon_app)
                        .placeholder(R.drawable.icon_app)
                        .into(binding.myPageProfilePhoto)
                    binding.myPageProfileName.text = this.nickname
                    binding.myPageProfileEmail.text = this.email
                }

                // 유저가 쓴 글이 있으면 그 데이터를 리사이클러뷰에 넘겨준다
                if (tempWritingList.isNotEmpty()) {
                    writingList = tempWritingList[0]
                    writingAdapter.updateList(writingList)
                }

                // 유저가 북마크한 글이 있으면 그 데이터를 리사이클러뷰에 넘겨준다
                if (tempWBookMarkList.isNotEmpty()) {
                    wBookMarkList = tempWBookMarkList[0]
                    wBookMarkAdapter.updateList(wBookMarkList)
                }

                // 유저가 북마크한 서점이 있으면 그 데이터를 리사이클러뷰에 넘겨준다
                if (tempSBookMarkList.isNotEmpty()) {
                    sBookMarkList = tempSBookMarkList[0]
                    Log.d("로그", "sBookMarkList: $sBookMarkList")
                    sBookMarkAdapter.updateList(sBookMarkList)
                }
            }

            // 실패
            else -> Log.d("로그", "유저 정보 불러오기 실패 - message: ${response.message}")

        }
    }

    override fun onGetUserFailure(message: String) {
        Log.d("로그", "onGetUserFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("네트워크 확인 후 다시 시도해주세요.")
    }

    // 유저 이미지 url 전달
    override fun provideImgUrl() = this.userImgUrl
}