package com.albineli.udacity.popularmovies.ui

import android.content.Context
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.enums.RequestStatusDescriptor

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT


class RequestStatusView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    //@BindView(R.id.llRequestStatusError)
    internal var mErrorContainerView: LinearLayout? = null

    //@BindView(R.id.btRequestStatusRetry)
    internal var mTryAgainButton: Button? = null

    //@BindView(R.id.pbRequestStatusLoading)
    internal var mLoadingProgressBar: ProgressBar? = null

    //@BindView(R.id.tvRequestStatusEmptyMessage)
    internal var mEmptyMessageTextView: TextView? = null

    @RequestStatusDescriptor.RequestStatus
    private var mRequestStatus: Int = 0

    private var mTryAgainClickListener: ITryAgainClickListener? = null

    init {

        initializeViews(context)
    }

    private fun initializeViews(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.request_status, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        //ButterKnife.bind(this);
    }

    fun setRequestStatus(requestStatus: Int, matchParentHeight: Boolean) {
        this.mRequestStatus = requestStatus
        redrawStatus(matchParentHeight)
    }

    fun setEmptyMessage(@StringRes messageResId: Int) {
        mEmptyMessageTextView!!.setText(messageResId)
    }

    private fun redrawStatus(matchParentHeight: Boolean) {
        toggleStatus(mRequestStatus == RequestStatusDescriptor.LOADING,
                mRequestStatus == RequestStatusDescriptor.ERROR,
                mRequestStatus == RequestStatusDescriptor.EMPTY,
                if (matchParentHeight) MATCH_PARENT else WRAP_CONTENT)
    }

    private fun toggleStatus(loadingVisible: Boolean, errorVisible: Boolean, emptyMessageVisible: Boolean, viewHeight: Int) {
        mLoadingProgressBar!!.visibility = if (loadingVisible) View.VISIBLE else View.INVISIBLE
        mErrorContainerView!!.visibility = if (errorVisible) View.VISIBLE else View.INVISIBLE
        mEmptyMessageTextView!!.visibility = if (emptyMessageVisible) View.VISIBLE else View.INVISIBLE

        mTryAgainButton!!.setOnClickListener {
            if (mTryAgainClickListener != null) {
                mTryAgainClickListener!!.tryAgain()
            }
        }

        val layoutParams = this.layoutParams
        layoutParams.height = viewHeight
        this.layoutParams = layoutParams
    }

    fun setTryAgainClickListener(tryAgainClickListener: ITryAgainClickListener) {
        mTryAgainClickListener = tryAgainClickListener
    }

    interface ITryAgainClickListener {
        fun tryAgain()
    }
}
