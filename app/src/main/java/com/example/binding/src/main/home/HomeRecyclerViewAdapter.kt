package com.example.binding.src.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.binding.R
import com.example.binding.src.main.home.models.GetBooksResponse

class HomeRecyclerViewAdapter(private val itemList: ArrayList<GetBooksResponse>): RecyclerView.Adapter<HomeRecyclerViewAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeRecyclerViewAdapter.HomeViewHolder {
        val viewHolder = HomeViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_menu, parent, false)
        )
        return viewHolder
    }


    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: HomeRecyclerViewAdapter.HomeViewHolder, position: Int) {
        holder.bindValue()
    }

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // private val view: View = lodgeView.recycler_show_lodge_root

        fun bindValue(){}
    }
}