package com.makeus6.binding.src.main.my_page.settings.report

import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.makeus6.binding.R
import com.makeus6.binding.config.BaseActivity
import com.makeus6.binding.databinding.ActivitySettingsProfileBinding
import com.makeus6.binding.databinding.ActivitySettingsReportBinding
import com.makeus6.binding.src.main.my_page.settings.models.GetProfileResponse
import com.makeus6.binding.src.main.my_page.settings.profile.SettingsProfileActivityView
import com.makeus6.binding.src.main.my_page.settings.profile.SettingsProfileService

class SettingsReportActivity : BaseActivity<ActivitySettingsReportBinding>(
    ActivitySettingsReportBinding::inflate
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.settingsReportLeft.setOnClickListener {
            finish()
        }
    }
}