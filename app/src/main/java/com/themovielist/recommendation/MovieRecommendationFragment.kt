package com.themovielist.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.themovielist.R
import com.themovielist.base.BaseFragment
import com.themovielist.base.BasePresenter
import com.themovielist.browse.MovieBrowseFragment
import com.themovielist.home.partiallist.HomeMovieListFragment
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieModel
import com.themovielist.model.view.MovieListViewModel
import com.themovielist.movielist.MovieListFragment
import timber.log.Timber
import javax.inject.Inject

class MovieRecommendationFragment : BaseFragment<MovieRecommendationContract.View>(), MovieRecommendationContract.View {
    override val presenterImplementation: BasePresenter<MovieRecommendationContract.View>
        get() = mPresenter
    override val viewImplementation: MovieRecommendationContract.View
        get() = this

    var movieId: Int? = null
        set(value) {
            mPresenter.setMovieId(value)
        }

    @Inject
    lateinit var mPresenter: MovieRecommendationPresenter

    private lateinit var mMovieRecommendationList: HomeMovieListFragment

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.movie_recommendation_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMovieRecommendationList = addFragmentIfNotExists(childFragmentManager, R.id.flMovieRecommendationListContainer, MOVIE_RECCOMENDATIONLIST_FRAGMENT) {
            HomeMovieListFragment.getInstance()
        }

        val movieCastViewModel = buildMovieCastViewModel(savedInstanceState)
        mPresenter.start(movieCastViewModel)
    }

    private fun buildMovieCastViewModel(savedInstanceState: Bundle?): MovieListViewModel {
        return if (savedInstanceState == null) {
            MovieListViewModel()
        } else {
            savedInstanceState.getParcelable(MOVIE_LIST_VIEW_MODEL_BUNDLE_KEY)
        }
    }

    override fun showErrorLoadingMovieCast(error: Throwable) {
        Timber.e(error,"An error occurred while tried to fetch the movie recommendations: ${error.message}")
        mMovieRecommendationList.showErrorLoadingMovieList(error)
    }

    override fun showMovieRecommendationList(movieRecommendationList: List<MovieModel>) {
        mMovieRecommendationList.addMovies(movieRecommendationList)
    }

    override fun hideLoadingIndicator() {
        mMovieRecommendationList.hideLoadingIndicator()
    }

    override fun showLoadingIndicator() {
        mMovieRecommendationList.showLoadingIndicator()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
       /* if (this.mPresenter.movieListViewModel != null) {

        }*/
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    companion object {
        const val MOVIE_LIST_VIEW_MODEL_BUNDLE_KEY = "movie_list_view_model"
        const val MOVIE_RECCOMENDATIONLIST_FRAGMENT = "MOVIE_RECCOMENDATIONLIST_FRAGMENT"
    }
}
