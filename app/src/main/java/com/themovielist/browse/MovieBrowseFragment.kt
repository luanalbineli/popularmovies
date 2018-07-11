package com.themovielist.browse

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.themovielist.R
import com.themovielist.base.BaseFragment
import com.themovielist.base.BasePresenter
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieCastViewModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.movielist.MovieListFragment
import com.themovielist.ui.searchabletoolbar.OnSearchToolbarQueryChanged
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.movie_browse_fragment.*
import javax.inject.Inject

class MovieBrowseFragment : BaseFragment<MovieBrowseContract.View>(), MovieBrowseContract.View, OnSearchToolbarQueryChanged {
    override val presenterImplementation: BasePresenter<MovieBrowseContract.View>
        get() = mPresenter

    override val viewImplementation: MovieBrowseContract.View
        get() = this

    @Inject
    lateinit var mPresenter: MovieBrowsePresenter

    lateinit var mMovieListFragment: MovieListFragment

    private val mQueryChangedHandler = Handler(Looper.getMainLooper())
    private lateinit var mPerformQueryRunnable: Runnable

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_browse_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureComponents()

        mMovieListFragment.onReadyToConfigure = {
            val movieCastViewModel = buildMovieCastViewModel(savedInstanceState)
            mPresenter.start(movieCastViewModel)

            mMovieListFragment.useListLayout()
        }
    }

    private fun configureComponents() {
        mMovieListFragment = addFragmentIfNotExists(childFragmentManager, R.id.flMovieListContainer, BROWSE_MOVIE_LIST_FRAGMENT, {
            MovieListFragment.getInstance()
        })

        mMovieListFragment.onTryAgainListener = {
            mPresenter.tryAgain()
        }
    }

    private fun buildMovieCastViewModel(savedInstanceState: Bundle?): MovieCastViewModel {
        return if (savedInstanceState == null) {
            MovieCastViewModel()
        } else {
            savedInstanceState.getParcelable(MOVIE_CAST_VIEW_MODEL_BUNDLE_KEY)
        }
    }

    override fun showMovieList(movieList: List<MovieImageGenreViewModel>, configurationImageResponseModel: ConfigurationImageResponseModel) {
        mMovieListFragment.replaceMoviesToList(movieList, configurationImageResponseModel)
    }

    private fun toggleBackDropAndListVisibility(backDropVisible: Boolean) {
        clMovieBrowseBackdropContainer.setDisplay(backDropVisible)
        flMovieListContainer.setDisplay(!backDropVisible)
    }

    override fun showErrorLoadingQueryResult(error: Throwable) {
        mMovieListFragment.showErrorLoadingMovies()
    }

    override fun hideLoadingIndicator() {
        mMovieListFragment.hideLoadingIndicator()
    }

    override fun showLoadingIndicator() {
        mMovieListFragment.showLoadingIndicator()
        toggleBackDropAndListVisibility(false)
    }

    override fun onChange(query: String) {
        if (::mPerformQueryRunnable.isInitialized) {
            mQueryChangedHandler.removeCallbacks(mPerformQueryRunnable)
        }

        mPerformQueryRunnable = Runnable {
            mPresenter.onQueryChanged(query)
        }

        mQueryChangedHandler.postDelayed(mPerformQueryRunnable, QUERY_WAIT_CHANGE_MILLISECONDS)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    companion object {
        const val MOVIE_CAST_VIEW_MODEL_BUNDLE_KEY = "movie_cast_view_model"
        const val BROWSE_MOVIE_LIST_FRAGMENT = "browse_movie_list_fragment"
        const val QUERY_WAIT_CHANGE_MILLISECONDS = 300L

        fun getInstance(): MovieBrowseFragment {
            return MovieBrowseFragment()
        }
    }
}
