package com.makeus6.binding.src.main

import android.os.Bundle
import com.makeus6.binding.R
import com.makeus6.binding.config.BaseActivity
import com.makeus6.binding.databinding.ActivityMainBinding
import com.makeus6.binding.src.main.home.HomeFragment
import com.makeus6.binding.src.main.menu.MenuFragment
import com.makeus6.binding.src.main.my_page.MyPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 처음 화면이 켜졌을 때 활성화될 하단 버튼 - 실제 화면이 아닌 버튼에만 적용
        binding.mainBtmNav.selectedItemId = R.id.menu_main_btm_nav_home

        // 메뉴 프래그먼트로 진입
        supportFragmentManager.beginTransaction()
            .add(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        // 아이콘 틴트는 셀렉터로 적용
        binding.mainBtmNav.itemIconTintList = null

        // 탭을 선택했을 때 프래그먼트 화면 전환
        binding.mainBtmNav.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_main_btm_nav_home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, HomeFragment())
                            .commitAllowingStateLoss()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.menu_main_btm_nav_menu -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, MenuFragment())
                            .commitAllowingStateLoss()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.menu_main_btm_nav_my_page -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, MyPageFragment())
                            .commitAllowingStateLoss()
                    }
                }
                false
            })
    }
}