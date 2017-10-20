package com.themovielist.ui.recyclerview

import android.support.annotation.StringRes
import android.view.View
import com.themovielist.enums.RequestStatusDescriptor
import timber.log.Timber


class GridStatusViewHolder internal constructor(itemView: View, tryAgainClick: (() -> Unit)?, @StringRes emptyMessageResId: Int) : CustomRecyclerViewHolder(itemView) {
    init {
        /*rsvRequestStatus.setEmptyMessage(emptyMessageResId)
        rsvRequestStatus.setTryAgainClickListener(tryAgainClick)*/
    }

    fun bind(@RequestStatusDescriptor.RequestStatus requestStatus: Int, numberOfItems: Int) {
        /*rsvRequestStatus.setRequestStatus(requestStatus, numberOfItems == 0)*/
        Timber.i("REDRAWING GRID STATUS: " + requestStatus)
    }
}
