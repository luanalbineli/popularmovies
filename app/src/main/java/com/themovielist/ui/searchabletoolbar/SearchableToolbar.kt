package com.themovielist.ui.searchabletoolbar

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.themovielist.R
import kotlinx.android.synthetic.main.toolbar_title.view.*


class SearchableToolbar constructor(context: Context, attributeSet: AttributeSet) : Toolbar(context, attributeSet) {
    var onSearchToolbarQueryChanged: OnSearchToolbarQueryChanged? = null
        set(value) {
            if (field != null) {
                toggleEditTextFocused(false)
            }
            field = value
        }
    private val mFrameLayout by lazy {
        LayoutInflater.from(context).inflate(R.layout.toolbar_title, this, false) as ConstraintLayout
    }

    init {
        super.setTitle(null) // Clear the default TextView title
        addView(mFrameLayout)

        tvToolbarTitle.setOnClickListener {
            toggleEditTextFocused(true)
        }

        etToolbarSearchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onSearchToolbarQueryChanged?.onChange(s.toString())
            }

        })

        tvToolbarCancelSearch.setOnClickListener { toggleEditTextFocused(false) }
        tvToolbarClearSearch.setOnClickListener { etToolbarSearchText.text = null }
    }

    private fun toggleEditTextFocused(focused: Boolean) {
        if (onSearchToolbarQueryChanged == null) {
            return
        }

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