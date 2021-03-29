package com.medium.binding.src.main.home.room

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medium.binding.R
import com.makeramen.roundedimageview.RoundedImageView
import com.medium.binding.config.ApplicationClass
import com.medium.binding.src.main.home.models.CommentsResult
import kotlinx.android.synthetic.main.item_post.view.*

class CommentsRecyclerAdapter(private val homeRoomActivity: HomeRoomActivity
): RecyclerView.Adapter<CommentsRecyclerAdapter.CommentsHolder>() {

    private var commentsList = ArrayList<CommentsResult>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsRecyclerAdapter.CommentsHolder {
        val viewHolder = CommentsHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false)
        )
        return viewHolder
    }


    override fun getItemCount() = commentsList.size


    override fun onBindViewHolder(holder: CommentsRecyclerAdapter.CommentsHolder, position: Int) {

        // 사용자 선택에 따라 최신글 or 인기글 데이터 연결
        holder.bindCommentsValue(commentsList[position], position)
    }

    inner class CommentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView
        private val markEmpty: ImageView = itemView.item_post_bookmark_empty    // 북마크 안한 거
        private val markFilled: ImageView = itemView.item_post_bookmark_filled  // 북마크 한 거
        private val comments: TextView = itemView.item_post_text            // 글 내용
        private val postDate: TextView = itemView.item_post_date            // 글 쓴 날짜
        private val nickname: TextView = itemView.item_post_nickname        // 닉네임
        private val userImg: RoundedImageView = itemView.item_post_photo    // 프로필 사진
        private val report: TextView = itemView.item_post_report            // 신고
        private val edit: TextView = itemView.item_post_edit                // 수정 - 자기 글일 때만
        private val delete: TextView = itemView.item_post_delete            // 삭제 - 자기 글일 때만

        // 최신글
        fun bindCommentsValue(commentsData: CommentsResult, itemPos: Int){

            // 북마크
            if(commentsData.isBookMark == 1){
                markFilled.visibility = View.VISIBLE
            }else{
                markFilled.visibility = View.INVISIBLE
            }

            postDate.text = commentsData.createdAt      // 글 쓴 날짜
            nickname.text = commentsData.nickname       // 닉네임
            // 프로필 사진이 있으면 적용
            if(commentsData.userImgUrl != "-1"){
                Glide.with(homeRoomActivity)
                    .load(commentsData.userImgUrl)
                    .error(R.drawable.icon_app)
                    .into(userImg)
            }

            // 자기 글이면 수정, 삭제 버튼, 아니면 신고 버튼 보이게 함
            // 버튼 리스너
            if(commentsData.userIdx == ApplicationClass.userIdx){
                report.visibility = View.INVISIBLE
                edit.visibility = View.VISIBLE
                delete.visibility = View.VISIBLE

                edit.setOnClickListener {  }    // 수정 버튼
                delete.setOnClickListener{}     // 삭제 버튼
            }else{
                report.visibility = View.VISIBLE
                edit.visibility = View.INVISIBLE
                delete.visibility = View.INVISIBLE

                report.setOnClickListener {  }  // 신고 버튼
            }

            // 글 내용
            comments.text = commentsData.contents

            // 북마크 설정
            markEmpty.setOnClickListener{
                commentsData.contentsIdx?.let{
                    homeRoomActivity.showLoadingDialog(homeRoomActivity)
                    Log.d("로그", "itemPos: $itemPos")
                    HomeRoomService(homeRoomActivity).tryPatchWBookmark(it, itemPos)
                }
            }
            // 북마크 해제
            markFilled.setOnClickListener {
                commentsData.contentsIdx?.let {
                    homeRoomActivity.showLoadingDialog(homeRoomActivity)
                    Log.d("로그", "itemPos: $itemPos")
                    HomeRoomService(homeRoomActivity).tryPatchWBookmark(it, itemPos)
                }
            }
        }
    }

    fun updateList(nList: ArrayList<CommentsResult>){
        commentsList = nList
        notifyDataSetChanged()
    }

    // 1. 북마크 수정
    fun updateItem(pos: Int, bookmark: Int){
        commentsList[pos].isBookMark = bookmark
        notifyItemChanged(pos)
    }
}