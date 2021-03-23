package com.makeus6.binding.src.main.my_page.settings

import android.os.Bundle
import android.view.View
import com.makeus6.binding.R
import com.makeus6.binding.config.BaseFragment
import com.makeus6.binding.databinding.FragmentSettingsBinding
import com.makeus6.binding.src.main.my_page.MyPageFragmentView


class SettingsFragment(private val myPageFragment: MyPageFragmentView): BaseFragment<FragmentSettingsBinding>(
    FragmentSettingsBinding::bind,
    R.layout.fragment_settings
){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private val onClickSettings = View.OnClickListener {

    }
}