package com.medium.binding.src.main.my_page.my_post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.medium.binding.R
import com.medium.binding.src.main.my_page.models.CommentsWriting
import kotlinx.android.synthetic.main.item_bookshelf.view.*
import kotlinx.android.synthetic.main.item_post.view.*


class MyWritingRecyclerAdapter(val mContext: Context):
    RecyclerView.Adapter<MyWritingRecyclerAdapter.WritingViewHolder>() {

    private var writingList = arrayListOf<CommentsWriting>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyWritingRecyclerAdapter.WritingViewHolder {
        val viewHolder = WritingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false)
        )
        return viewHolder
    }


    override fun getItemCount() = writingList.size

    override fun onBindViewHolder(holder: MyWritingRecyclerAdapter.WritingViewHolder, position: Int) {
        holder.bindValue(writingList[position])
    }

    inner class WritingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val item: View = itemView
        private val nickname: TextView = itemView.item_post_nickname        // 유저 닉네임
        private val userImg: RoundedImageView = itemView.item_post_photo    // 유저 이미지
        private val postedDate: TextView = itemView.item_post_date          // 포스팅 데이트
        private val contents: TextView = itemView.item_post_text
        private val edit: TextView = itemView.item_post_edit
        private val delete: TextView = itemView.item_post_delete

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

            edit.setOnClickListener {  }
            delete.setOnClickListener {  }
        }
    }


    fun updateList(newList: ArrayList<CommentsWriting>){
        writingList = newList
        this.notifyDataSetChanged()
    }
}

