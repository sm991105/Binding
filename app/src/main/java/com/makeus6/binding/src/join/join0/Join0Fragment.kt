package com.makeus6.binding.src.join.join0

import android.os.Bundle
import android.view.View
import com.makeus6.binding.R
import com.makeus6.binding.config.BaseFragment
import com.makeus6.binding.databinding.FragmentJoin0Binding
import com.makeus6.binding.src.join.JoinActivity

class Join0Fragment: BaseFragment<FragmentJoin0Binding>(
    FragmentJoin0Binding::bind,
    R.layout.fragment_join_0
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.join0Back.setOnClickListener {
            (activity!! as JoinActivity).onBackPressed()
        }

        binding.join0Btn.setOnClickListener {
            (activity!! as JoinActivity).moveToJoin1()
        }
    }

}