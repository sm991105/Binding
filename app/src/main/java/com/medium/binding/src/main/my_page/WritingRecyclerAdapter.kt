package com.medium.binding.src.main.my_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medium.binding.R
import com.medium.binding.src.main.menu.store_detail.StoreDetailFragment
import com.medium.binding.src.main.my_page.models.WritingData
import com.medium.binding.src.main.my_page.my_post.MyPostFragment
import kotlinx.android.synthetic.main.item_bookshelf.view.*


class WritingRecyclerAdapter(fragment: MyPageFragment):
    RecyclerView.Adapter<WritingRecyclerAdapter.WritingViewHolder>() {

    private var writingList = arrayListOf<WritingData>()
    private val myPageFragment = fragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WritingRecyclerAdapter.WritingViewHolder {
        val viewHolder = WritingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bookshelf, parent, false)
        )
        return viewHolder
    }


    override fun getItemCount() = writingList.size

    override fun onBindViewHolder(holder: WritingRecyclerAdapter.WritingViewHolder, position: Int) {
        holder.bindValue(writingList[position])
    }

    inner class WritingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val item: View = itemView
        private val bookName: TextView = itemView.item_bookshelf_title

        fun bindValue(writing: WritingData){
            bookName.text = writing.bookName

            //나중에 클릭 -> 북마크 글 보는 기능
            item.setOnClickListener{
                val fragmentManager = myPageFragment.childFragmentManager
                val myPostFragment = MyPostFragment()
                val idxBundle = Bundle()
                idxBundle.putInt("bookIdx", writing.bookIdx)
                myPostFragment.arguments = idxBundle
                fragmentManager.beginTransaction()
                    .addToBackStack("MyPost")
                    .add(R.id.my_page_frm, myPostFragment)
                    .commitAllowingStateLoss()
            }
        }
    }


    fun updateList(newList: ArrayList<WritingData>){
        writingList = newList
        this.notifyDataSetChanged()
    }
}