package com.example.binding.src.main.home

import android.os.Bundle
import android.view.View
import com.example.binding.R
import com.example.binding.config.BaseFragment
import com.example.binding.databinding.FragmentHomeBinding
import com.example.binding.databinding.FragmentJoin2Binding

class HomeFragment: BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}