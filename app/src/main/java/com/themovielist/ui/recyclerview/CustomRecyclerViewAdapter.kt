package com.themovielist.ui.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.themovielist.R
import com.themovielist.enums.RequestStatusDescriptor
import timber.log.Timber
import java.security.InvalidParameterException
import java.util.*

abstract class CustomRecyclerViewAdapter<TItem, THolder : CustomRecyclerViewHolder> private constructor(private val mItems: MutableList<TItem>) : RecyclerView.Adapter<CustomRecyclerViewHolder>() {
    private var mOnItemClickListener: ((position: Int, item: TItem) -> Unit)? = null
    @RequestStatusDescriptor.RequestStatus private var mRequestStatus = RequestStatusDescriptor.HIDDEN

    private var mTryAgainClickListener: (() -> Unit)? = null
    private var mEmptyMessageResId = R.string.the_list_is_empty

    protected constructor() : this(ArrayList<TItem>())

    protected constructor(tryAgainClickListener: (() -> Unit)?) : this() {
        mTryAgainClickListener = tryAgainClickListener
    }

    protected constructor(@StringRes emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)?) : this() {
        mEmptyMessageResId = emptyMessageResId
        mTryAgainClickListener = tryAgainClickListener
    }

    interface ViewType {
        companion object {
            const val GRID_STATUS = 0
            const val ITEM = 1
        }
    }

    val isStatusError: Boolean
        get() = mRequestStatus == RequestStatusDescriptor.ERROR

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomRecyclerViewHolder {
        if (viewType == ViewType.GRID_STATUS) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.grid_status, parent, false)
            return GridStatusViewHolder(itemView, mTryAgainClickListener, mEmptyMessageResId)
        }
        return onCreateItemViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: CustomRecyclerViewHolder, position: Int) {
        Timber.d("onBindViewHolder - Binding the view. Position: $position | GRID_STATUS: ${holder.itemViewType == ViewType.GRID_STATUS} | Holder: $holder")
        if (holder.itemViewType == ViewType.GRID_STATUS) {
            val gridStatusViewHolder = holder as GridStatusViewHolder
            gridStatusViewHolder.bind(mRequestStatus, mItems.size)
            return
        }

        onBindItemViewHolder(holder as THolder, position)
        holder.itemView.setOnClickListener {
            mOnItemClickListener?.invoke(holder.adapterPosition, mItems[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return mItems.size + if (mRequestStatus == RequestStatusDescriptor.HIDDEN) 0 else 1 // List status.
    }

    final override fun getItemViewType(position: Int): Int {
        if (position == mItems.size) {
            return ViewType.GRID_STATUS
        }
        val itemViewType = getItemViewTypeOverride(position)
        if (itemViewType == ViewType.GRID_STATUS) {
            throw InvalidParameterException("The view type must be different of ${ViewType.GRID_STATUS}")
        }
        return itemViewType
    }

    protected open fun getItemViewTypeOverride(position: Int): Int {
        return ViewType.ITEM
    }

    protected fun getItemByPosition(position: Int): TItem {
        return mItems[position]
    }

    fun addItems(items: List<TItem>) {
        hideRequestStatus()

        val itemCount = mItems.size
        mItems.addAll(items)
        notifyItemRangeInserted(itemCount, items.size)
        Timber.i("addItems - itemCount: $itemCount | items.size: ${items.size}")
    }

    fun replaceItems(items: List<TItem>) {
        redrawGridStatus(RequestStatusDescriptor.HIDDEN)

        clearItems()

        mItems.addAll(items)
        notifyItemRangeInserted(0, items.size)
    }

    fun clearItems() {
        val itemCount = mItems.size
        if (itemCount > 0) {
            mItems.clear()
            notifyItemRangeRemoved(0, itemCount)
        }
    }

    fun removeItemByIndex(index: Int) {
        mItems.removeAt(index)
        notifyItemRemoved(index)
    }

    val items: List<TItem>
        get() = mItems

    fun showLoading() {
        redrawGridStatus(RequestStatusDescriptor.LOADING)
        Timber.i("REDRAWING the grid status to SHOW the loading indicator")
    }

    fun hideRequestStatus() {
        redrawGridStatus(RequestStatusDescriptor.HIDDEN)
    }

    fun hideLoadingIndicator() {
        Timber.i("REDRAWING the grid status to HIDE the loading indicator: ${mRequestStatus == RequestStatusDescriptor.LOADING}")
        if (mRequestStatus == RequestStatusDescriptor.LOADING) { // Hide only if is loading
            hideRequestStatus()
        }
    }

    fun showEmptyMessage() {
        redrawGridStatus(RequestStatusDescriptor.EMPTY)
        Timber.i("REDRAWING the grid status as EMPTY")
    }

    fun showErrorMessage() {
        redrawGridStatus(RequestStatusDescriptor.ERROR)
        Timber.i("REDRAWING the grid status as ERROR")
    }

    private fun redrawGridStatus(gridStatus: Int) {
        val previousRequestStatus = mRequestStatus
        mRequestStatus = gridStatus
        if (mRequestStatus == RequestStatusDescriptor.HIDDEN) {
            notifyItemRemoved(mItems.size)
        } else if (previousRequestStatus == RequestStatusDescriptor.HIDDEN) {
            notifyItemInserted(mItems.size)
        } else {
            notifyItemChanged(mItems.size)
        }
    }

    fun setOnItemClickListener(onItemClickListener: (position: Int, item: TItem) -> Unit) {
        this.mOnItemClickListener = onItemClickListener
    }

    protected abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): THolder

    protected abstract fun onBindItemViewHolder(holder: THolder, position: Int)
}
