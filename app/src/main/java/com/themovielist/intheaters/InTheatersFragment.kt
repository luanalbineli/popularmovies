package com.themovielist.intheaters

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.base.BaseFragment
import com.themovielist.base.BasePresenter
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.movielist.MovieListFragment.Companion.getItensPerRow
import javax.inject.Inject


class InTheatersFragment : BaseFragment<InTheatersContract.View>(), InTheatersContract.View {

    override val presenterImplementation: BasePresenter<InTheatersContract.View>
        get() = mPresenter

    override val viewImplementation: InTheatersContract.View
        get() = this

    @Inject
    lateinit var mPresenter: InTheatersPresenter

    private val mMovieListAdapter by lazy { InTheatersAdapter(R.string.the_list_is_empty, { /*mPresenter.tryAgain()*/ })}

    private val mGridLayoutManager by lazy { GridLayoutManager(activity, getItensPerRow(activity)) }

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.in_theaters_fragment, container, false)
    }

    companion object {
        fun getInstance(): InTheatersFragment {
            return InTheatersFragment()
        }
    }
}
