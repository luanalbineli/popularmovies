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
import timber.log.Timber
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.browse)
        configureComponents()

        val movieCastViewModel = buildMovieCastViewModel(savedInstanceState)
        mPresenter.start(movieCastViewModel)
    }

    private fun configureComponents() {
        mMovieListFragment = childFragmentManager.findFragmentById(R.id.fragmentMovieListBrowse) as MovieListFragment
        mMovieListFragment.useListLayout()

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
        Timber.i("showMovieList - Adding the movie list to the recyclerview: ${movieList.size}")
        mMovieListFragment.addMoviesToList(movieList, configurationImageResponseModel)
    }

    override fun clearMovieList() {
        mMovieListFragment.clearMovieList()
    }

    private fun toggleBackDropAndListVisibility(backDropVisible: Boolean) {
        clMovieBrowseBackdropContainer.setDisplay(backDropVisible)
        flMovieListContainer.setDisplay(!backDropVisible)
    }

    override fun showErrorLoadingQueryResult(error: Throwable) {
        mMovieListFragment.showErrorLoadingMovies()
    }

    override fun showLoadingIndicator() {
        mMovieListFragment.showLoadingIndicator()
        toggleBackDropAndListVisibility(false)
    }

    override fun onChange(query: String) {
        Timber.i("onChange - Changing the query: $query")
        if (::mPerformQueryRunnable.isInitialized) {
            mQueryChangedHandler.removeCallbacks(mPerformQueryRunnable)
        }

        mPerformQueryRunnable = Runnable {
            mPresenter.onQueryChanged(query)
            Timber.i("onChange - Running the query: $query")
        }

        mQueryChangedHandler.postDelayed(mPerformQueryRunnable, QUERY_WAIT_CHANGE_MILLISECONDS)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    companion object {
        const val MOVIE_CAST_VIEW_MODEL_BUNDLE_KEY = "movie_cast_view_model"
        const val QUERY_WAIT_CHANGE_MILLISECONDS = 500L

        fun getInstance(): MovieBrowseFragment {
            return MovieBrowseFragment()
        }
    }
}
