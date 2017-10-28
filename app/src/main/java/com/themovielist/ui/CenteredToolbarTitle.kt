package com.themovielist.ui

import android.content.Context
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.albineli.udacity.popularmovies.R

class CenteredToolbarTitle constructor(context: Context, attributeSet: AttributeSet): Toolbar(context, attributeSet) {
    private val mCenteredTitleTextView by lazy {
        LayoutInflater.from(context).inflate(R.layout.toolbar_title, this, false) as TextView
    }


    init {
        super.setTitle(null) // Clear the default TextView title
        addView(mCenteredTitleTextView)
    }

    override fun setTitle(resId: Int) {
        this.title = context.getString(resId)
    }

    override fun setTitle(title: CharSequence?) {
        mCenteredTitleTextView.text = title
    }
}