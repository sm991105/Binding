package com.makeus6.binding.src.main.menu.store_detail

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class MRelativeLayout: RelativeLayout {
    constructor(context:Context) : super(context) {}
    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {}

    override fun performClick():Boolean {
        super.performClick()
        return true
    }
}


