package com.example.binding.src.main.my_page

import android.os.Bundle
import android.view.View
import com.example.binding.R
import com.example.binding.config.BaseFragment
import com.example.binding.databinding.FragmentMyPageBinding

class MyPageFragment: BaseFragment<FragmentMyPageBinding>(
    FragmentMyPageBinding::bind,
    R.layout.fragment_my_page
){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}