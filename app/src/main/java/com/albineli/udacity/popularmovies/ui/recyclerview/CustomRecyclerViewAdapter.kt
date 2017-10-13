package com.albineli.udacity.popularmovies.ui.recyclerview

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.enums.RequestStatusDescriptor
import com.albineli.udacity.popularmovies.ui.RequestStatusView
import timber.log.Timber
import java.util.*

abstract class CustomRecyclerViewAdapter<TItem, THolder : CustomRecyclerViewHolder> private constructor(private val mItems: MutableList<TItem>) : RecyclerView.Adapter<CustomRecyclerViewHolder>() {
    private var mOnItemClickListener: IListRecyclerViewItemClick<TItem>? = null
    @RequestStatusDescriptor.RequestStatus private var mRequestStatus = RequestStatusDescriptor.HIDDEN

    private var mTryAgainClickListener: RequestStatusView.ITryAgainClickListener? = null
    private var mEmptyMessageResId = R.string.the_list_is_empty

    protected constructor() : this(ArrayList<TItem>()) {}

    protected constructor(tryAgainClickListener: RequestStatusView.ITryAgainClickListener) : this() {

        mTryAgainClickListener = tryAgainClickListener
    }

    protected constructor(@StringRes emptyMessageResId: Int, tryAgainClickListener: RequestStatusView.ITryAgainClickListener) : this() {

        mEmptyMessageResId = emptyMessageResId
        mTryAgainClickListener = tryAgainClickListener
    }

    interface ViewType {
        companion object {
            val GRID_STATUS = 0
            val ITEM = 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomRecyclerViewHolder {
        if (viewType == ViewType.GRID_STATUS) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.grid_status, parent, false)
            return GridStatusViewHolder(itemView, mTryAgainClickListener!!, mEmptyMessageResId)
        }
        return onCreateItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: CustomRecyclerViewHolder, position: Int) {
        if (holder.itemViewType == ViewType.GRID_STATUS) {
            val gridStatusViewHolder = holder as GridStatusViewHolder
            gridStatusViewHolder.bind(mRequestStatus, mItems.size)
            return
        }


        onBindItemViewHolder(holder as THolder, position)
        holder.itemView.setOnClickListener {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onClick(holder.adapterPosition, mItems[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size + if (mRequestStatus == RequestStatusDescriptor.HIDDEN) 0 else 1 // List status.
    }

    override fun getItemViewType(position: Int): Int {
        if (position == mItems.size) {
            return ViewType.GRID_STATUS
        }
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

    fun showEmptyMessage() {
        redrawGridStatus(RequestStatusDescriptor.EMPTY)
    }

    fun showErrorMessage() {
        redrawGridStatus(RequestStatusDescriptor.ERROR)
    }

    private fun redrawGridStatus(gridStatus: Int) {
        Timber.i("REDRAWING THE GRID STATUS: " + gridStatus)
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

    fun setOnItemClickListener(onItemClickListener: IListRecyclerViewItemClick<TItem>) {
        this.mOnItemClickListener = onItemClickListener
    }

    interface IListRecyclerViewItemClick<in TItemInternal> {
        fun onClick(position: Int, item: TItemInternal)
    }

    protected abstract fun onCreateItemViewHolder(parent: ViewGroup): THolder

    protected abstract fun onBindItemViewHolder(holder: THolder, position: Int)
}
