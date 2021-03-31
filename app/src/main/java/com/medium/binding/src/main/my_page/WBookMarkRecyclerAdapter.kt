package com.medium.binding.src.main.my_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medium.binding.R
import com.medium.binding.src.main.my_page.models.WBookMarkData
import com.medium.binding.src.main.my_page.my_post.MyPostFragment
import kotlinx.android.synthetic.main.item_bookshelf.view.*


class WBookMarkRecyclerAdapter(fragment: MyPageFragment):
    RecyclerView.Adapter<WBookMarkRecyclerAdapter.WritingViewHolder>() {

    private var writingList = arrayListOf<WBookMarkData>()
    private val myPageFragment = fragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WBookMarkRecyclerAdapter.WritingViewHolder {
        val viewHolder = WritingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bookshelf, parent, false)
        )
        return viewHolder
    }


    override fun getItemCount() = writingList.size

    override fun onBindViewHolder(holder: WBookMarkRecyclerAdapter.WritingViewHolder, position: Int) {
        holder.bindValue(writingList[position])
    }

    inner class WritingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val item: View = itemView
        private val bookName: TextView = itemView.item_bookshelf_title

        fun bindValue(writing: WBookMarkData){
            bookName.text = writing.bookName

            // 나중에 클릭 -> 북마크 글 보는 기능
            item.setOnClickListener {
                val fragmentManager = myPageFragment.childFragmentManager
                val myPostFragment = MyPostFragment(MyPostFragment.BOOKMARK_POST)
                val idxBundle = Bundle()
                idxBundle.putInt("bookIdx", writing.bookIdx)
                myPostFragment.arguments = idxBundle
                fragmentManager.beginTransaction()
                    .addToBackStack("MyPost1")
                    .add(R.id.my_page_frm, myPostFragment)
                    .commitAllowingStateLoss()
            }
        }
    }


    fun updateList(newList: ArrayList<WBookMarkData>){
        writingList = newList
        this.notifyDataSetChanged()
    }
}