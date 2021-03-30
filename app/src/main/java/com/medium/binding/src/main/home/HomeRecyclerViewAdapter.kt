package com.medium.binding.src.main.home

import android.content.Intent
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medium.binding.R
import com.medium.binding.src.main.home.models.NewestResult
import com.medium.binding.src.main.home.models.PopularResult
import com.makeramen.roundedimageview.RoundedImageView
import com.medium.binding.config.ApplicationClass
import com.medium.binding.src.main.home.room.HomeRoomActivity
import kotlinx.android.synthetic.main.item_book.view.*

class HomeRecyclerViewAdapter(private val homeFragment: HomeFragment,
                              private var newestList: ArrayList<NewestResult>,
                              private var popularList: ArrayList<PopularResult>,
                              private var categoryFlag: Int
): RecyclerView.Adapter<HomeRecyclerViewAdapter.BookHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeRecyclerViewAdapter.BookHolder {
        val viewHolder = BookHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_book, parent, false)
        )
        return viewHolder
    }


    override fun getItemCount(): Int {
        return when(categoryFlag){
            0 -> newestList.size
            else -> popularList.size
        }
    }

    override fun onBindViewHolder(holder: HomeRecyclerViewAdapter.BookHolder, position: Int) {

        // 사용자 선택에 따라 최신글 or 인기글 데이터 연결
        if(categoryFlag == 0){
            holder.bindNewestValue(newestList[position])
        }else if(categoryFlag == 1){
            holder.bindPopularValue(popularList[position])
        }
    }

    inner class BookHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView
        private val bookTitle: TextView = itemView.item_book_title
        private val bookAuthor: TextView = itemView.item_book_author
        private val numOfPost: TextView = itemView.item_book_post_amount
        private val bookImg: RoundedImageView = itemView.item_book_image

        // 최신글
        fun bindNewestValue(bookData: NewestResult){
            bookTitle.text = bookData.bookName
            bookAuthor.text = bookData.authorName
            numOfPost.text = bookData.contentsCount

            Glide.with(homeFragment)
                .load(bookData.bookImgUrl)
                .error(R.drawable.icon_app)
                .placeholder(R.drawable.icon_app)
                .into(bookImg)

            view.setOnClickListener {

                // 중복 클릭 방지
                ApplicationClass.mLastClickTime.apply{
                    if (SystemClock.elapsedRealtime() - ApplicationClass.mLastClickTime.toInt() < 1000){
                        return@setOnClickListener
                    }
                    this.compareAndSet(this.toLong(), SystemClock.elapsedRealtime())
                }

                val bookRoomIntent = Intent(homeFragment.context, HomeRoomActivity::class.java)
                bookRoomIntent.putExtra("bookIdx", bookData.bookIdx)
                homeFragment.startActivity(bookRoomIntent)
            }
        }

        // 인기글
        fun bindPopularValue(bookData: PopularResult){
            bookTitle.text = bookData.bookName
            bookAuthor.text = bookData.authorName
            numOfPost.text = bookData.contentsCount

            Glide.with(homeFragment)
                .load(bookData.bookImgUrl)
                .error(R.drawable.icon_app)
                .placeholder(R.drawable.icon_app)
                .into(bookImg)

            view.setOnClickListener {

                ApplicationClass.mLastClickTime.apply{
                    if (SystemClock.elapsedRealtime() - ApplicationClass.mLastClickTime.toInt() < 1000){
                        return@setOnClickListener
                    }
                    this.compareAndSet(this.toLong(), SystemClock.elapsedRealtime())
                }

                val bookRoomIntent = Intent(homeFragment.context, HomeRoomActivity::class.java)
                bookRoomIntent.putExtra("bookIdx", bookData.bookIdx)
                homeFragment.startActivityForResult(bookRoomIntent, HomeFragment.BOOK_ENTERED_CODE)
            }
        }
    }

    fun updateNewest(nList: ArrayList<NewestResult>, flag: Int){
        newestList = nList
        categoryFlag = flag
        notifyDataSetChanged()
    }

    fun updatePopular(pList: ArrayList<PopularResult>, flag: Int){
        popularList = pList
        categoryFlag = flag
        notifyDataSetChanged()
    }
}