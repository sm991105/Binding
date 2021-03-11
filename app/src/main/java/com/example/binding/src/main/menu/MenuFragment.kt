package com.example.binding.src.main.menu

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.binding.R
import com.example.binding.config.BaseFragment
import com.example.binding.databinding.FragmentMenuBinding

class MenuFragment: BaseFragment<FragmentMenuBinding>(
    FragmentMenuBinding::bind,
    R.layout.fragment_menu
){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}