package com.medium.binding.src.main.my_page

import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseFragment
import com.medium.binding.databinding.FragmentMyPageBinding
import com.medium.binding.src.main.my_page.models.*
import com.medium.binding.src.main.my_page.settings.SettingsFragment
import java.util.*
import kotlin.collections.ArrayList

class MyPageFragment(): BaseFragment<FragmentMyPageBinding>(
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

        writingFlag = 0

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
                binding.myPageRecyclerBookmark.visibility = View.GONE
            }
        }

        // 북마크한 글 버튼 클릭
        binding.myPagePostBookmark.setOnClickListener{
            if(writingFlag != 1){
                writingFlag = 1

                // 글씨 위에 점 교체
                binding.myPagePostDotMine.visibility = View.INVISIBLE
                binding.myPagePostDotBookmark.visibility = View.VISIBLE

                // 텍스트 폰트 변경
                binding.myPagePostMine.typeface = fontKr
                binding.myPagePostBookmark.typeface = fontBold

                // 리사이클러 뷰 교체
                binding.myPageRecyclerMine.visibility = View.GONE
                binding.myPageRecyclerBookmark.visibility = View.VISIBLE
            }
        }

        binding.myPageBtnSettings.setOnClickListener(onClickSettings)
    }

    // 설정 버튼 클릭
    private val onClickSettings = View.OnClickListener {

        // 중복 클릭 방지
        ApplicationClass.mLastClickTime.apply {
            if (SystemClock.elapsedRealtime() - ApplicationClass.mLastClickTime.toInt() < 1000){
                return@OnClickListener
            }
            this.compareAndSet(this.toLong(), SystemClock.elapsedRealtime())
        }

        val settingsFragment = SettingsFragment(this)
        val cFragmentManager = childFragmentManager
        cFragmentManager.beginTransaction().apply{
            this.add(R.id.my_page_frm, settingsFragment)
                .addToBackStack("settings")
                .commitAllowingStateLoss()
        }
    }

    override fun onGetUserSuccess(response: GetUserResponse) {
        dismissLoadingDialog()

        when(response.code){

            // 유저 정보 불러오기 성공
            1000 -> {
                val result = response.result

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
                userInfo[0].apply {
                    this@MyPageFragment?.let{
                        it.userImgUrl = this.userImgUrl

                        Glide.with(it)
                            .load(this.userImgUrl)
                            .error(R.drawable.icon_app)
                            .placeholder(R.drawable.icon_app)
                            .into(binding.myPageProfilePhoto)
                        binding.myPageProfileName.text = this.nickname
                        binding.myPageProfileEmail.text = this.email
                    }
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
                    sBookMarkAdapter.updateList(sBookMarkList)
                }
            }

            // 실패
            else -> {
                showCustomToast(
                    "프로필 정보를 받아오던 중 에러가 발생했습니다\n" +
                            "에러가 계속되면 관리자에게 문의해주세요"
                )
            }

        }
    }

    override fun onGetUserFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("프로필 정보를 받아오던 중 에러가 발생했습니다\n" +
                "에러가 계속되면 관리자에게 문의해주세요"
        )
    }

    // 유저 이미지 url 전달
    override fun provideImgUrl() = this.userImgUrl
}