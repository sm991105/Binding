package com.example.binding.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.example.binding.databinding.DialogJoinBinding
import com.example.binding.src.join.JoinActivity
import com.example.binding.src.login.LoginActivity

class JoinDialog(context: Context, private val isSuccess: Boolean) : Dialog(context) {
    private lateinit var binding: DialogJoinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable())
        window!!.setDimAmount(0.2f)

        // 로그인 화면으로 돌아간다
        binding.dialogBtnLogin.setOnClickListener {
            val loginIntent = Intent(ownerActivity, LoginActivity::class.java)
            (ownerActivity as JoinActivity).let{
                it.moveToLogin(isSuccess)
                it.finish()
            }
        }
    }

    override fun show() {
        if(!this.isShowing) super.show()
    }
}