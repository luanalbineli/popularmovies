package com.themovielist.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.themovielist.R
import com.themovielist.ui.recyclerview.HorizonalSpaceItemDecoration
import kotlinx.android.synthetic.main.recycler_view.*

abstract class BaseRecyclerViewFragment<TView>: BaseFragment<TView>() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.recycler_view, container, false)

    protected fun useLinearLayoutManager(): LinearLayoutManager {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        rvRecyclerView.layoutManager = linearLayoutManager

        return linearLayoutManager
    }

    protected fun useHorizontalSpaceDecorator() {
        activity?.let {
            val dividerAmountOfSpace = it.resources.getDimension(R.dimen.home_movie_list_image_space)
            val spaceItemViewDecoration = HorizonalSpaceItemDecoration(dividerAmountOfSpace.toInt())
            rvRecyclerView.addItemDecoration(spaceItemViewDecoration)
        }
    }
}