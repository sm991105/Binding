package com.medium.binding.src.main.my_page

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
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
import com.medium.binding.src.main.menu.store_detail.StoreDetailFragment
import com.medium.binding.src.main.my_page.models.SBookMarkData
import kotlinx.android.synthetic.main.item_bookmark_store.view.*


class SBookMarkRecyclerAdapter(fragment: MyPageFragment):
    RecyclerView.Adapter<SBookMarkRecyclerAdapter.StoreViewHolder>() {

    private var storeList = arrayListOf<SBookMarkData>()
    private val myPageFragment = fragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SBookMarkRecyclerAdapter.StoreViewHolder {
        val viewHolder = StoreViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bookmark_store, parent, false)
        )
        return viewHolder
    }


    override fun getItemCount() = storeList.size

    override fun onBindViewHolder(holder: SBookMarkRecyclerAdapter.StoreViewHolder, position: Int) {
        holder.bindValue(storeList[position])
    }

    inner class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val item: View = itemView
        private val photo: RoundedImageView = itemView.item_bookmark_store_photo
        private val storeName: TextView = itemView.item_bookmark_store_name
        private val bookmark: ImageView = itemView.item_bookmark

        fun bindValue(store: SBookMarkData){
            Log.d("로그", "bindValue() called")

            // 북마크 아이콘과 겹치는 부분을 약간 어둡게 처리
            photo.setColorFilter(
                Color.parseColor("#BDBDBD"),
                PorterDuff.Mode.MULTIPLY
            )

            Glide.with(myPageFragment)
                .load(store.storeImgUrl)
                .error(R.drawable.icon_app)
                .into(photo)

            storeName.text = store.storeName

            // 클릭 -> 서점 화면 여는 기능
            item.setOnClickListener{
                val fragmentManager = myPageFragment.childFragmentManager
                val storeDetailFragment = StoreDetailFragment()
                val idxBundle = Bundle()
                idxBundle.putInt("bookStoreIdx", store.bookstoreIdx)
                storeDetailFragment.arguments = idxBundle
                fragmentManager.beginTransaction()
                    .addToBackStack("storeDetail")
                    .add(R.id.my_page_frm, storeDetailFragment)
                    .commitAllowingStateLoss()
            }
        }
    }

    fun updateList(newList: ArrayList<SBookMarkData>){
        storeList = newList
        this.notifyDataSetChanged()
    }
}