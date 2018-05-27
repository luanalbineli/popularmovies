package com.themovielist.ui.recyclerview

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
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
            val GRID_STATUS = 0
            val ITEM = 1
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

    open protected fun getItemViewTypeOverride(position: Int): Int {
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

    fun insertItemByIndex(item: TItem, index: Int) {
        mItems.add(index, item)
        notifyItemInserted(index)
    }

    val items: List<TItem>
        get() = mItems

    fun showLoading() {
        redrawGridStatus(RequestStatusDescriptor.LOADING)
    }

    fun hideRequestStatus() {
        redrawGridStatus(RequestStatusDescriptor.HIDDEN)
    }

    fun hideLoadingIndicator() {
        if (mRequestStatus == RequestStatusDescriptor.LOADING) { // Hide only if is loading
            hideRequestStatus()
        }
    }

    fun showEmptyMessage() {
        redrawGridStatus(RequestStatusDescriptor.EMPTY)
    }

    fun showErrorMessage() {
        redrawGridStatus(RequestStatusDescriptor.ERROR)
    }


    private fun redrawGridStatus(gridStatus: Int) {
        Timber.i("REDRAWING THE GRID STATUS: $gridStatus")
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
