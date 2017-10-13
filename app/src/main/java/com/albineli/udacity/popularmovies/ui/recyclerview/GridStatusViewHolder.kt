package com.albineli.udacity.popularmovies.ui.recyclerview

import android.support.annotation.StringRes
import android.view.View
import com.albineli.udacity.popularmovies.enums.RequestStatusDescriptor
import com.albineli.udacity.popularmovies.ui.RequestStatusView
import timber.log.Timber


class GridStatusViewHolder internal constructor(itemView: View, tryAgainClick: RequestStatusView.ITryAgainClickListener, @StringRes emptyMessageResId: Int) : CustomRecyclerViewHolder(itemView) {

    //@BindView(R.id.rsvRequestStatus)
    internal var mRequestStatusView: RequestStatusView? = null

    init {

        //ButterKnife.bind(this, itemView);

        mRequestStatusView!!.setEmptyMessage(emptyMessageResId)
        mRequestStatusView!!.setTryAgainClickListener(tryAgainClick)
    }

    fun bind(@RequestStatusDescriptor.RequestStatus requestStatus: Int, numberOfItems: Int) {
        mRequestStatusView!!.setRequestStatus(requestStatus, numberOfItems == 0)
        Timber.i("REDRAWING GRID STATUS: " + requestStatus)
    }
}
