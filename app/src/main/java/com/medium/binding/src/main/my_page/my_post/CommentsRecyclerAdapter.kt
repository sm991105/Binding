package com.medium.binding.src.main.my_page.my_post

import android.content.Context
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
import com.medium.binding.src.main.my_page.models.CommentsWriting
import kotlinx.android.synthetic.main.item_post.view.*


class CommentsRecyclerAdapter(val mContext: Context):
    RecyclerView.Adapter<CommentsRecyclerAdapter.WritingViewHolder>() {

    private var writingList = arrayListOf<CommentsWriting>()

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

    inner class WritingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                edit.setOnClickListener {  }
                delete.setOnClickListener {  }
            }
            // 다른 유저 글이면 신고, 북마크 설정/해제 가능
            else{
                edit.visibility = View.INVISIBLE
                delete.visibility = View.INVISIBLE
                report.visibility = View.VISIBLE

                report.setOnClickListener {  }

                markFilled.setOnClickListener {  }
                markEmpty.setOnClickListener {  }

            }

        }
    }


    fun updateList(newList: ArrayList<CommentsWriting>){
        writingList = newList
        this.notifyDataSetChanged()
    }
}

