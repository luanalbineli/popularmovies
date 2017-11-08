package com.themovielist.base

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.ui.recyclerview.HorizonalSpaceItemDecoration
import kotlinx.android.synthetic.main.recycler_view.*

abstract class BaseRecyclerViewFragment<TView>: BaseFragment<TView>() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recycler_view, container, false)
    }

    protected fun useLinearLayoutManager(): LinearLayoutManager {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        rvRecyclerView.layoutManager = linearLayoutManager

        return linearLayoutManager
    }

    protected fun useHorizontalSpaceDecorator() {
        val dividerAmountOfSpace = activity.resources.getDimension(R.dimen.home_movie_list_image_space)
        val spaceItemViewDecoration = HorizonalSpaceItemDecoration(dividerAmountOfSpace.toInt())
        rvRecyclerView.addItemDecoration(spaceItemViewDecoration)
    }
}