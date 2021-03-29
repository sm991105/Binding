package com.medium.binding.src.main.my_page.settings.report

import android.os.Bundle
import com.medium.binding.config.BaseActivity
import com.medium.binding.databinding.ActivitySettingsReportBinding

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