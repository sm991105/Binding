package com.medium.binding.src.main.my_page.my_post

import android.content.Context
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.src.main.home.models.CommentsBody
import com.medium.binding.src.main.home.room.HomeRoomActivity
import com.medium.binding.src.main.home.room.create.HomeCreateFragment
import com.medium.binding.src.main.home.room.remove.RemoveDialog
import com.medium.binding.src.main.home.room.report.ReportDialog
import com.medium.binding.src.main.my_page.MyPageFragment
import com.medium.binding.src.main.my_page.models.CommentsWriting
import com.medium.binding.util.Comments
import kotlinx.android.synthetic.main.item_post.view.*


class CommentsRecyclerAdapter(val fragment: MyPostFragment,
                              val mContext: Context,
                              val commentsListener: Comments.ClickListener):
    RecyclerView.Adapter<CommentsRecyclerAdapter.WritingViewHolder>() {

    private var writingList = arrayListOf<CommentsWriting>()

    var removeDialog: RemoveDialog? = null
    var reportDialog: ReportDialog? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsRecyclerAdapter.WritingViewHolder {
        val viewHolder = WritingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false)
        )
        return viewHolder
    }


    override fun getItemCount() = writingList.size

    override fun onBindViewHolder(holder: CommentsRecyclerAdapter.WritingViewHolder, position: Int) {
        holder.bindValue(writingList[position])
    }

    inner class WritingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    Comments.AdapterRemoveListener, Comments.AdapterReportListener{
        private val item: View = itemView
        private val nickname: TextView = itemView.item_post_nickname        // 유저 닉네임
        private val userImg: RoundedImageView = itemView.item_post_photo    // 유저 이미지
        private val postedDate: TextView = itemView.item_post_date          // 포스팅 데이트
        private val contents: TextView = itemView.item_post_text            // 유저 글
        private val report: TextView = itemView.item_post_report            // 신고
        private val edit: TextView = itemView.item_post_edit                // 수정
        private val delete: TextView = itemView.item_post_delete            // 삭제
        private val markFilled: ImageView = itemView.item_post_bookmark_filled  // 북마크 ON 됨
        private val markEmpty: ImageView = itemView.item_post_bookmark_empty  // 북마크 OFF 됨
        var contentsIdx: Int = -1

        fun bindValue(writing: CommentsWriting){
            // 프로필 사진
            if(writing.userImgUrl != "-1"){
                Glide.with(mContext)
                    .load(writing.userImgUrl)
                    .error(R.drawable.icon_app)
                    .into(userImg)
            }

            nickname.text = writing.nickname    // 닉네임
            postedDate.text = writing.createdAt // 포스팅 날짜
            contents.text = writing.contents    // 글

            // 유저가 쓴 글이면 수정, 삭제 가능
            if(ApplicationClass.userIdx == writing.userIdx){

                // 수정 버튼 -> 수정(발행)창을 연다
                edit.setOnClickListener {
                    val myPageFragment = fragment.parentFragment as MyPageFragment
                    myPageFragment.childFragmentManager.beginTransaction()
                        .replace(R.id.my_page_frm, HomeCreateFragment(
                                commentsListener,
                                writing.contents,
                                HomeRoomActivity.COMMENTS_EDIT,
                                writing.contentsIdx)
                        )
                        .addToBackStack("HomeCreate")
                        .commitAllowingStateLoss()
                }

                // 삭제 버튼
                delete.setOnClickListener {

                    // 중복 클릭 방지
                    ApplicationClass.mLastClickTime.apply {
                        if (SystemClock.elapsedRealtime() - ApplicationClass.mLastClickTime.toInt() < 1000) {
                            return@setOnClickListener
                        }
                        this.compareAndSet(this.toLong(), SystemClock.elapsedRealtime())
                    }

                    contentsIdx = writing.contentsIdx ?: -1
                    removeDialog = RemoveDialog(mContext, this)
                    removeDialog?.show()
                }
            }
            // 다른 유저 글이면 신고, 북마크 설정/해제 가능
            else{
                edit.visibility = View.INVISIBLE
                delete.visibility = View.INVISIBLE
                report.visibility = View.VISIBLE

                report.setOnClickListener {

                    // 중복 클릭 방지
                    ApplicationClass.mLastClickTime.apply {
                        if (SystemClock.elapsedRealtime() - ApplicationClass.mLastClickTime.toInt() < 1000) {
                            return@setOnClickListener
                        }
                        this.compareAndSet(this.toLong(), SystemClock.elapsedRealtime())
                    }

                    contentsIdx = writing.contentsIdx ?: -1
                    // 신고 다이얼로그
                    reportDialog = ReportDialog(mContext, this)
                    reportDialog?.show()

                }

                markFilled.setOnClickListener {  }
                markEmpty.setOnClickListener {  }

            }

            // 북마크 되어있으면 표시
            if(writing.isBookMark == 1){
                markFilled.visibility = View.VISIBLE
            }

        }

        override fun onClickRemove() {
            commentsListener.onClickRemove(contentsIdx)
        }

        override fun onClickReport(reportReason: String) {
            commentsListener.onClickReport(reportReason, contentsIdx)
        }
    }


    fun updateList(newList: ArrayList<CommentsWriting>){
        writingList = newList
        this.notifyDataSetChanged()
    }
}

