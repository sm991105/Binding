package com.example.binding.src.main

import android.os.Bundle
import com.example.binding.config.BaseActivity
import com.example.binding.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}