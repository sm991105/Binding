package com.medium.binding.src.main.menu

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medium.binding.R
import com.medium.binding.src.main.menu.models.StoresResult
import com.medium.binding.src.main.menu.store_detail.StoreDetailFragment
import com.makeramen.roundedimageview.RoundedImageView
import com.medium.binding.config.ApplicationClass
import kotlinx.android.synthetic.main.item_menu.view.*
import java.util.*

class MenuRecyclerViewAdapter(fragment: MenuFragment):
    RecyclerView.Adapter<MenuRecyclerViewAdapter.StoreViewHolder>() {

    private var storeList = arrayListOf<StoresResult>()
    private val menuFragment = fragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuRecyclerViewAdapter.StoreViewHolder {
        val viewHolder = StoreViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_menu, parent, false)
        )
        return viewHolder
    }


    override fun getItemCount() = storeList.size

    override fun onBindViewHolder(holder: MenuRecyclerViewAdapter.StoreViewHolder, position: Int) {
        holder.bindValue(storeList[position])
    }

    inner class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val item: View = itemView
        private val photo: RoundedImageView = itemView.item_menu_photo
        private val title: TextView = itemView.item_menu_title
        private val address: TextView = itemView.item_menu_address

        fun bindValue(store: StoresResult){
            Glide.with(menuFragment)
                .load(store.storeImgUrl)
                .error(R.drawable.icon_app)
                .into(photo)

            title.text = store.storeName
            address.text = store.location

            // 상세페이지로 이동
            item.setOnClickListener{

                // 중복 클릭 방지
                ApplicationClass.mLastClickTime.apply{
                    if (SystemClock.elapsedRealtime() - ApplicationClass.mLastClickTime.toInt() < 1000){
                        return@setOnClickListener
                    }
                    this.compareAndSet(this.toLong(), SystemClock.elapsedRealtime())
                }


                val fragmentManager = menuFragment.childFragmentManager
                val storeDetailFragment = StoreDetailFragment()
                val idxBundle = Bundle()
                idxBundle.putInt("bookStoreIdx", store.bookstoreIdx)
                storeDetailFragment.arguments = idxBundle
                fragmentManager.beginTransaction()
                    .addToBackStack("storeDetail")
                    .add(R.id.menu_frm, storeDetailFragment)
                    .commitAllowingStateLoss()
            }
        }
    }


    // 데이터 변경
    fun updateList(newList: ArrayList<StoresResult>){
        storeList = newList
        this.notifyDataSetChanged()
    }
}