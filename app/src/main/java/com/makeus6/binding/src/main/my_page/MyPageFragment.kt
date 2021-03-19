package com.makeus6.binding.src.main.my_page

import android.os.Bundle
import android.view.View
import com.makeus6.binding.R
import com.makeus6.binding.config.BaseFragment
import com.makeus6.binding.databinding.FragmentMyPageBinding

class MyPageFragment: BaseFragment<FragmentMyPageBinding>(
    FragmentMyPageBinding::bind,
    R.layout.fragment_home
){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}