package com.medium.binding.src.main.my_page.settings.profile

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.annotation.NonNull
import com.medium.binding.R

class ChangeImageDialog(@NonNull mContext: Context,
                        private val remove: View.OnClickListener,
                        private val choose: View.OnClickListener,
                        private var isImgOn: Boolean
                     ): Dialog(mContext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //다이얼로그의 꼭짓점이 짤리는부분 방지.
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_change_profile)

        // 다이얼로그 크기 설정
        val params: WindowManager.LayoutParams = this.window?.attributes!!
        val widthSize = ((context.resources.displayMetrics.widthPixels) * 0.88).toInt()
        val heightSize = ((context.resources.displayMetrics.heightPixels) * 0.16).toInt()
        val density: Float = context.resources.displayMetrics.density   // 기기 density
        val maxWidthPx = (360 * density + 0.5).toInt()      // 360dp -> 픽셀 변환
        val maxHeightPx = (135 * density + 0.5).toInt()     // 135dp -> 픽셀 변환
        // 스크린 너비의 88%가 360dp보다 크면 360dp로 고정
        if(widthSize > maxWidthPx){
            params.width = maxWidthPx
        }else{
            params.width = widthSize
        }
        // 스크린 높이의 16%가 135dp보다 크면 135dp로 고정
        if(heightSize > maxHeightPx){
            params.height = maxHeightPx
        }else{
            params.height = heightSize
        }
        params.gravity = Gravity.BOTTOM
        this.window?.attributes = params

        // 버튼 이벤트
        val btnRemove: TextView = findViewById(R.id.dialog_change_profile_remove)
        val btnChoose: TextView = findViewById(R.id.dialog_change_profile_choose)

        // 프로필 사진이 없으면 사진 삭제 버튼 비활성화
        btnRemove.isEnabled = isImgOn

        btnRemove.setOnClickListener(remove)
        btnChoose.setOnClickListener(choose)
    }
}