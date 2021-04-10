package com.medium.binding.util

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.medium.binding.config.ApplicationClass

object General {

    // 키보드 숨기기
    fun hideKeyboard(context: Context, v: View){
        val iMm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        iMm.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()
    }

    // 중복 클릭 방지
    fun isDoubledClicked(): Boolean{
        ApplicationClass.mLastClickTime.apply{
            if (SystemClock.elapsedRealtime() - ApplicationClass.mLastClickTime.toInt() < 1000){
                return true
            }

            this.compareAndSet(this.toLong(), SystemClock.elapsedRealtime())
            return false
        }
    }
}