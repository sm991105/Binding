package com.medium.binding.src.main.my_page.settings

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class MLinearLayout: LinearLayout {
    constructor(context:Context) : super(context) {}
    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {}

    override fun performClick():Boolean {
        super.performClick()
        return true
    }
}


