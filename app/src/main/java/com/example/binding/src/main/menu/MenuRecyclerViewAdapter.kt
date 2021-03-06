package com.example.binding.src.main.menu

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.binding.R
import com.example.binding.src.main.home.HomeRecyclerViewAdapter
import com.example.binding.src.main.home.models.GetBooksResponse
import com.example.binding.src.main.menu.models.StoresResult
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.item_menu.view.*

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
         private val photo: RoundedImageView = itemView.item_menu_photo
         private val title: TextView = itemView.item_menu_title
         private val address: TextView = itemView.item_menu_address

        fun bindValue(store: StoresResult){
            Glide.with(menuFragment)
                .load(store.storeImgUrl)
                .error(R.drawable.jangu)
                .into(photo)

            title.text = store.storeName
            address.text = store.location
        }
    }

    fun updateList(newList: ArrayList<StoresResult>){
        storeList = newList
        this.notifyDataSetChanged()
    }
}