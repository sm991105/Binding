package com.medium.binding.src.join.join0

import android.os.Bundle
import android.view.View
import com.medium.binding.R
import com.medium.binding.config.BaseFragment
import com.medium.binding.databinding.FragmentJoin0Binding
import com.medium.binding.src.join.JoinActivity

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