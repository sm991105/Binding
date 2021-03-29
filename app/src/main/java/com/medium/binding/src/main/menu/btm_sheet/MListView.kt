package com.medium.binding.src.main.menu.btm_sheet

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.AbsListView
import android.widget.ListView


class MListView(context: Context, p_attrs: AttributeSet): ListView(context, p_attrs) {
    override fun performClick():Boolean {
        super.performClick()
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (canScrollVertically(this)) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (canScrollVertically(this)) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return super.onTouchEvent(ev)
    }

    private fun canScrollVertically(view: AbsListView?): Boolean {
        var canScroll = false
        if (view != null && view.childCount > 0) {
            val isOnTop = view.firstVisiblePosition != 0 || view.getChildAt(0).top != 0
            val isAllItemsVisible = isOnTop && view.lastVisiblePosition == view.childCount
            if (isOnTop || isAllItemsVisible) {
                canScroll = true
            }
        }
        return canScroll
    }
}