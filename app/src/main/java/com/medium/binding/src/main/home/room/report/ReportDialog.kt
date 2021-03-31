package com.medium.binding.src.main.home.room.report

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
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import com.medium.binding.R
import com.medium.binding.databinding.ActivityMainBinding
import com.medium.binding.databinding.DialogRemoveBinding
import com.medium.binding.databinding.DialogReportBinding
import com.medium.binding.databinding.LayoutBottomSheetBinding
import com.medium.binding.src.main.home.room.ReportDialogListener

class ReportDialog(@NonNull val mContext: Context,
                   private val activityListener: ReportDialogListener): Dialog(mContext) {
    lateinit var binding: DialogReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //다이얼로그의 꼭짓점이 짤리는부분 방지.
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_report)

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

        binding = DialogReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 아니요
        binding.dialogReportNo.setOnClickListener{
            dismiss()
        }
        // 신고하기
        binding.dialogReportYes.setOnClickListener{
            val reason = binding.dialogReportReason.text.toString()
            Log.d("로그", "reason: $reason")
            if(reason.length < 5 || reason.length > 500){
                Toast.makeText(mContext, "신고사유를 5~500자로 입력해주세요",
                    Toast.LENGTH_SHORT).show()
            }else{
                activityListener.onClickReport(reason)
            }
        }
    }
}