package com.makeus6.binding.src.main.home.create_room

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
import com.makeus6.binding.R
import com.makeus6.binding.databinding.DialogCreateRoomBinding


class CreateRoomDialog(context: Context) : DialogFragment() {
    private var _binding: DialogCreateRoomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateRoomBinding.inflate(inflater, container, false)

        val dialog = dialog;
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val view: View = activity!!.layoutInflater.inflate(R.layout.dialog_create_room, null)
        builder.setView(view)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // dialog?.setCanceledOnTouchOutside(true)

        // 만들기 버튼 클릭
        val btn = view.findViewById<Button>(R.id.dialog_create_room_btn)
        btn.setOnClickListener {
            dismiss()
        }

        return dialog
    }
}