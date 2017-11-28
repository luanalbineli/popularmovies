package com.themovielist.home.fulllist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.albineli.udacity.popularmovies.R
import com.themovielist.base.BaseDaggerActivity
import com.themovielist.base.BasePresenter
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.model.view.HomeFullMovieListViewModel
import com.themovielist.moviedetail.MovieDetailActivity
import com.themovielist.movielist.MovieListFragment
import kotlinx.android.synthetic.main.upcoming_movies_fragment.*
import timber.log.Timber
import javax.inject.Inject


class HomeFullMovieListActivity : BaseDaggerActivity<HomeFullMovieListContract.View>(), HomeFullMovieListContract.View {
    override val presenterImplementation: BasePresenter<HomeFullMovieListContract.View>
        get() = mPresenter

    override val viewImplementation: HomeFullMovieListContract.View
        get() = this
    @Inject
    lateinit var mPresenter: HomeFullMovieListPresenter

    private lateinit var mUpcomingMovieListFragment: MovieListFragment

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.upcoming_movies_fragment)

        val fragmentInstance = fragmentManager.findFragmentById(R.id.fragmentUpcomingMovieList)

        mUpcomingMovieListFragment = fragmentInstance as MovieListFragment
        mUpcomingMovieListFragment.onTryAgainListener = {
            mPresenter.tryAgain()
        }

        mUpcomingMovieListFragment.onClickMovieItem = { _, movieWithGenreModel ->
            mPresenter.showMovieDetail(movieWithGenreModel)
        }

        mUpcomingMovieListFragment.onLoadMoreMovies = {
            Timber.d("Trying to load more movies")
            mPresenter.loadMoreMovies()
        }

        glvGenreList.onSelectGenreListener = { _, genreListItemModel ->
            mPresenter.onChangeSelectedGenre(genreListItemModel)
        }

        val upcomingMoviesViewModel = savedInstanceState?.getParcelable<HomeFullMovieListViewModel>(UPCOMING_MOVIES_VIEW_MODEL)

        mPresenter.start(upcomingMoviesViewModel)
    }

    override fun showMovieDetail(movieWithGenreModel: MovieWithGenreModel) {
        val movieDetailIntent = MovieDetailActivity.getDefaultIntent(this, movieWithGenreModel.movieModel)
        startActivity(movieDetailIntent)
    }

    override fun addUpcomingMovieList(upcomingMovieList: List<MovieWithGenreModel>, configurationResponseModel: ConfigurationImageResponseModel) {
        mUpcomingMovieListFragment.addMoviesToList(upcomingMovieList, configurationResponseModel)
    }

    override fun showGenreList(genreListItemList: List<GenreListItemModel>) {
        glvGenreList.showGenreList(genreListItemList)
    }

    override fun hideLoadingIndicator() {
        mUpcomingMovieListFragment.hideLoadingIndicator()
    }

    override fun scrollToItemPosition(firstVisibleItemPosition: Int) {
        mUpcomingMovieListFragment.scrollToItemPosition(firstVisibleItemPosition)
    }

    override fun onStop() {
        super.onStop()
        val firstVisibleItemPosition = mUpcomingMovieListFragment.getFirstVisibleItemPosition()
        mPresenter.onStop(firstVisibleItemPosition)
    }

    override fun showErrorLoadingUpcomingMovies(error: Throwable) {
        Timber.e(error, "An error occurred while tried to load the upcoming movies")
        mUpcomingMovieListFragment.showErrorLoadingMovies()
    }

    override fun showLoadingUpcomingMoviesIndicator() {
        mUpcomingMovieListFragment.showLoadingIndicator()
    }

    override fun enableLoadMoreListener() {
        mUpcomingMovieListFragment.enableLoadMoreListener()
    }

    override fun disableLoadMoreListener() {
        mUpcomingMovieListFragment.disableLoadMoreListener()
    }

    override fun showEmptyListMessage() {
        mUpcomingMovieListFragment.showEmptyListMessage()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mPresenter.upcomingMoviesViewModel?.let {
            outState?.putParcelable(UPCOMING_MOVIES_VIEW_MODEL, it)
        }
    }

    override fun replaceUpcomingMovieList(finalUpcomingMovieList: List<MovieWithGenreModel>, imageResponseModel: ConfigurationImageResponseModel) {
        mUpcomingMovieListFragment.replaceMoviesToList(finalUpcomingMovieList, imageResponseModel)
    }

    companion object {
        private const val UPCOMING_MOVIES_VIEW_MODEL = "upcoming_movies_view_model"
        private const val HOME_MOVIE_SORT = "home_movie_sort"

        fun getInstance(): HomeFullMovieListActivity = HomeFullMovieListActivity()
        fun getIntent(context: Context, homeMovieSort: Int): Intent =
                Intent(context, HomeFullMovieListActivity::class.java).also {
                    it.putExtra(HOME_MOVIE_SORT, homeMovieSort)
                }
    }
}