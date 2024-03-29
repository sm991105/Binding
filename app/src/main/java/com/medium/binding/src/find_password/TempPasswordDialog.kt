package com.medium.binding.src.find_password

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.medium.binding.R
import com.medium.binding.databinding.DialogJoinDoneBinding


class TempPasswordDialog(context: Context) : DialogFragment() {
    private var _binding: DialogJoinDoneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogJoinDoneBinding.inflate(inflater, container, false)

        val dialog = dialog;
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false);

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val view: View = activity!!.layoutInflater.inflate(R.layout.dialog_temp_password, null)
        builder.setView(view)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)

        // 로그인하러 가기 버튼
        val btn = view.findViewById<Button>(R.id.dialog_temp_password_btn)
        btn.setOnClickListener {
            (activity!! as FindPasswordActivity).moveToLogin()
        }

        return dialog
    }
}