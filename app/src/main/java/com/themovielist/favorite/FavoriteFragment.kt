package com.themovielist.favorite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.themovielist.R
import com.themovielist.base.BasePresenter
import com.themovielist.base.BaseRecyclerViewFragment
import com.themovielist.enums.HomeMovieSortEnum
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.moviedetail.MovieDetailActivity
import com.themovielist.movielist.MovieListFragment
import kotlinx.android.synthetic.main.activity_home_full_movie_list.*
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


class FavoriteFragment : BaseRecyclerViewFragment<HomeFullMovieListContract.View>(), HomeFullMovieListContract.View {
    override val presenterImplementation: BasePresenter<HomeFullMovieListContract.View>
        get() = mPresenter

    override val viewImplementation: HomeFullMovieListContract.View
        get() = this
    @Inject
    lateinit var mPresenter: HomeFullMovieListPresenter

    private lateinit var mMovieListFragment: MovieListFragment

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.favorite_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      /*  val filter = intent.getIntExtra(HOME_MOVIE_SORT, Int.MIN_VALUE)
        if (filter == Int.MIN_VALUE) {
            throw InvalidParameterException(HOME_MOVIE_SORT)
        }

        setContentView(R.layout.activity_main)

        vsMainContent.layoutResource = R.layout.activity_home_full_movie_list
        vsMainContent.inflate()

        configureComponents()

        val upcomingMoviesViewModel = savedInstanceState?.getParcelable<HomeFullMovieListViewModel>(UPCOMING_MOVIES_VIEW_MODEL)

        mPresenter.start(upcomingMoviesViewModel, filter)*/
    }

    private fun configureComponents() {

        val fragmentInstance = fragmentManager.findFragmentById(R.id.fragmentUpcomingMovieList)

        mMovieListFragment = fragmentInstance as MovieListFragment
        mMovieListFragment.onTryAgainListener = {
            mPresenter.tryAgain()
        }

        mMovieListFragment.onClickMovieItem = { _, movieWithGenreModel ->
            mPresenter.showMovieDetail(movieWithGenreModel)
        }

        mMovieListFragment.onLoadMoreMovies = {
            Timber.d("Trying to load more movies")
            mPresenter.loadMoreMovies()
        }

        mMovieListFragment.onChangeListViewType = { useListViewType ->
            mPresenter.onChangeListViewType(useListViewType)
        }

        glvGenreList.onSelectGenreListener = { _, genreListItemModel ->
            mPresenter.onChangeSelectedGenre(genreListItemModel)
        }
    }

    override fun setTitleByFilter(filter: Int) {
        toolbar.title = getString(if (filter == HomeMovieSortEnum.POPULAR) {
            R.string.popular
        } else {
          R.string.rating
        })
    }

    override fun setListViewType(useListViewType: Boolean) {
        if (useListViewType) {
            mMovieListFragment.useListLayout()
        } else {
            mMovieListFragment.useGridLayout()
        }
    }

    override fun showMovieDetail(movieImageGenreViewModel: MovieImageGenreViewModel) {
        val movieDetailIntent = MovieDetailActivity.getDefaultIntent(activity, movieImageGenreViewModel.movieModel)
        startActivity(movieDetailIntent)
    }

    override fun addMoviesToList(movieList: List<MovieImageGenreViewModel>, configurationResponseModel: ConfigurationImageResponseModel) {
        mMovieListFragment.addMoviesToList(movieList, configurationResponseModel)
    }

    override fun showGenreList(genreListItemList: List<GenreListItemModel>) {
        glvGenreList.showGenreList(genreListItemList)
    }

    override fun hideLoadingIndicator() {
        mMovieListFragment.hideLoadingIndicator()
    }

    override fun scrollToItemPosition(firstVisibleItemPosition: Int) {
        mMovieListFragment.scrollToItemPosition(firstVisibleItemPosition)
    }

    override fun onStop() {
        super.onStop()
        val firstVisibleItemPosition = mMovieListFragment.getFirstVisibleItemPosition()
        mPresenter.onStop(firstVisibleItemPosition)
    }

    override fun showErrorLoadingUpcomingMovies(error: Throwable) {
        Timber.e(error, "An error occurred while tried to load the upcoming movies")
        mMovieListFragment.showErrorLoadingMovies()
    }

    override fun showLoadingUpcomingMoviesIndicator() {
        mMovieListFragment.showLoadingIndicator()
    }

    override fun enableLoadMoreListener() {
        mMovieListFragment.enableLoadMoreListener()
    }

    override fun disableLoadMoreListener() {
        mMovieListFragment.disableLoadMoreListener()
    }

    override fun showEmptyListMessage() {
        mMovieListFragment.showEmptyListMessage()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mPresenter.upcomingMoviesViewModel?.let {
            outState?.putParcelable(UPCOMING_MOVIES_VIEW_MODEL, it)
        }
    }

    override fun replaceMovieList(movieList: List<MovieImageGenreViewModel>, imageResponseModel: ConfigurationImageResponseModel) {
        mMovieListFragment.replaceMoviesToList(movieList, imageResponseModel)
    }

    companion object {
        private const val UPCOMING_MOVIES_VIEW_MODEL = "upcoming_movies_view_model"
        private const val HOME_MOVIE_SORT = "home_movie_sort"

        fun getInstance(): FavoriteFragment = FavoriteFragment()
        fun getIntent(context: Context, homeMovieSort: Int): Intent =
                Intent(context, FavoriteFragment::class.java).also {
                    it.putExtra(HOME_MOVIE_SORT, homeMovieSort)
                }
    }
}