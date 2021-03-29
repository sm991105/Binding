package com.medium.binding.src.main.my_page.settings.logout

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.annotation.NonNull
import com.medium.binding.R

class LogoutDialog(@NonNull mContext: Context,
private val confirm: View.OnClickListener,
private val cancel: View.OnClickListener): Dialog(mContext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //다이얼로그의 꼭짓점이 짤리는부분 방지.
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_logout)

        // 다이얼로그 크기 설정
        val params: WindowManager.LayoutParams = this.window?.attributes!!
        val widthSize = ((context.resources.displayMetrics.widthPixels) * 0.88).toInt()
        val density: Float = context.resources.displayMetrics.density   // 기기 density
        val maxWidthPx = (360 * density + 0.5).toInt()      // 360dp -> 픽셀로 변환
        if(widthSize > maxWidthPx){
            params.width = maxWidthPx
        }else{
            params.width = widthSize
        }
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        this.window?.attributes = params
        Log.d("로그", "real dialog width: ${this.window?.attributes!!.width}")

        // 버튼 이벤트
        val btnCancel: Button = findViewById(R.id.dialog_logout_no)
        val btnLogout: Button = findViewById(R.id.dialog_logout_yes)

        btnCancel.setOnClickListener(cancel)
        btnLogout.setOnClickListener(confirm)
    }
}