package com.themovielist.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.albineli.udacity.popularmovies.R
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.movie_detail_section.view.*
import java.security.InvalidParameterException


class MovieDetailSectionView<TModel> constructor(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    private val mLayoutInflater by lazy { LayoutInflater.from(context) }
    private val mTitleText: String

    private val mContentLayoutResId: Int

    private var mButtonTitle: String

    private var mEmptyMessage: String

    var onBindSectionContent: ((View, TModel) -> Unit)? = null

    var onClickSectionButton: (() -> Unit)? = null

    init {
        mLayoutInflater.inflate(R.layout.movie_detail_section, this)
        val attributes = context.theme.obtainStyledAttributes(attributeSet, R.styleable.MovieDetailSectionView, 0, 0)

        try {
            mContentLayoutResId = attributes.getResourceId(R.styleable.MovieDetailSectionView_content, -1)

            if (mContentLayoutResId == -1) {
                throw InvalidParameterException("The layout is required")
            }

            mTitleText = attributes.getString(R.styleable.MovieDetailSectionView_title)
            mButtonTitle = attributes.getString(R.styleable.MovieDetailSectionView_buttonTitle)
            mEmptyMessage = attributes.getString(R.styleable.MovieDetailSectionView_emptyMessage)
        } finally {
            attributes.recycle()
        }

        tvMovieDetailSectionTitle.text = mTitleText
        tvMovieDetailEmptyMessage.text = mEmptyMessage
    }

    fun bind(sectionList: List<TModel>) {
        if (sectionList.isNotEmpty()) {
            bindSectionContent(sectionList)
        } else {
            tvMovieDetailSectionContent.setDisplay(false)
        }
    }

    private fun bindSectionContent(sectionList: List<TModel>) {
        tvMovieDetailEmptyMessage.setDisplay(false)
        if (sectionList.size > 1) {
            tvMovieDetailSectionButton.text = String.format(mButtonTitle, sectionList.size)
            tvMovieDetailSectionButton.setOnClickListener { onClickSectionButton?.invoke() }
        } else {
            tvMovieDetailSectionButton.setDisplay(false)
        }

        val sectionContentView = mLayoutInflater.inflate(mContentLayoutResId, tvMovieDetailSectionContent)
        onBindSectionContent?.invoke(sectionContentView, sectionList.first())
    }
}