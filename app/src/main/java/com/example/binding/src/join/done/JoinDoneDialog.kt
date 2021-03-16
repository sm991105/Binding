package com.example.binding.src.join.done

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
import com.example.binding.R
import com.example.binding.databinding.DialogJoinDoneBinding
import com.example.binding.src.join.JoinActivity


class JoinDoneDialog(context: Context, private val isSuccess: Boolean) : DialogFragment() {
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
        val view: View = activity!!.layoutInflater.inflate(R.layout.dialog_join_done, null)
        builder.setView(view)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)

        // 로그인하러 가기 버튼
        val btn = view.findViewById<Button>(R.id.dialog_btn_login)
        btn.setOnClickListener {
            (activity as JoinActivity).let{
                it.moveToLogin(isSuccess)
                it.finish()
            }
        }

        return dialog
    }
}