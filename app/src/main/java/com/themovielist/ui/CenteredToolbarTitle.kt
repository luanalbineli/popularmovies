package com.themovielist.ui

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.themovielist.R
import kotlinx.android.synthetic.main.toolbar_title.view.*


class CenteredToolbarTitle constructor(context: Context, attributeSet: AttributeSet): Toolbar(context, attributeSet) {
    private val mFrameLayout by lazy {
        LayoutInflater.from(context).inflate(R.layout.toolbar_title, this, false) as ConstraintLayout
    }

    init {
        super.setTitle(null) // Clear the default TextView title
        addView(mFrameLayout)

        tvToolbarTitle.setOnClickListener {
            toggleEditTextFocused(true)
        }

        tvToolbarCancelSearch.setOnClickListener { toggleEditTextFocused(false) }
        tvToolbarClearSearch.setOnClickListener { etToolbarSearchText.text = null }
    }

    private fun toggleEditTextFocused(focused: Boolean) {
        tvToolbarTitle.visibility = if (focused) View.GONE else View.VISIBLE
        clToolbarSearchContainer.visibility = if (focused) View.VISIBLE else View.GONE

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (focused) {
            etToolbarSearchText.requestFocus()
            imm.showSoftInput(etToolbarSearchText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            imm.hideSoftInputFromWindow(etToolbarSearchText.windowToken, 0)
        }
    }

    override fun setTitle(resId: Int) {
        this.title = context.getString(resId)
    }

    override fun setTitle(title: CharSequence?) {
        tvToolbarTitle.text = title
    }
}