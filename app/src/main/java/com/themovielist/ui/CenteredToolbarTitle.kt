package com.themovielist.ui

import android.content.Context
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.themovielist.R
import kotlinx.android.synthetic.main.toolbar_title.view.*

class CenteredToolbarTitle constructor(context: Context, attributeSet: AttributeSet): Toolbar(context, attributeSet) {
    private val mFrameLayout by lazy {
        LayoutInflater.from(context).inflate(R.layout.toolbar_title, this, false) as FrameLayout
    }

    init {
        super.setTitle(null) // Clear the default TextView title
        addView(mFrameLayout)
    }

    override fun setTitle(resId: Int) {
        this.title = context.getString(resId)
    }

    override fun setTitle(title: CharSequence?) {
        toolbar_title.text = title
    }
}