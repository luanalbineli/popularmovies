package com.themovielist.favorite

import android.os.Bundle
import android.view.*
import com.themovielist.R
import com.themovielist.base.BasePresenter
import com.themovielist.base.BaseRecyclerViewFragment
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.HomeFullMovieListViewModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.moviedetail.MovieDetailActivity
import com.themovielist.movielist.MovieListFragment
import timber.log.Timber
import javax.inject.Inject


class FavoriteFragment : BaseRecyclerViewFragment<FavoriteContract.View>(), FavoriteContract.View {
    override val presenterImplementation: BasePresenter<FavoriteContract.View>
        get() = mPresenter

    override val viewImplementation: FavoriteContract.View
        get() = this
    @Inject
    lateinit var mPresenter: FavoritePresenter

    private lateinit var mMovieListFragment: MovieListFragment

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.favorite_sort, menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.favorite_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setTitle(R.string.favorite)

        configureComponents()

        mMovieListFragment.onReadyToConfigure = {
            val viewModel = savedInstanceState?.getParcelable<HomeFullMovieListViewModel>(UPCOMING_MOVIES_VIEW_MODEL)
            mPresenter.start(viewModel)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.favorite_movie_sort) {
            mPresenter.handleSortMenuClick()
            return true
        }
        return false
    }

    override fun openDialogToSelectListSort(selectedSort: Int) {
     /*   MaterialDialog.Builder(activity)
                .title(R.string.select_the_sort)
                .items(getString(R.string.added_order), getString(R.string.release_date))
                .itemsCallbackSingleChoice(selectedSort, {_, _, _, _ -> true })
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive { _, which ->
                    mPresenter.onChangeListSort(which.ordinal)
                }
                .show()*/
    }

    private fun configureComponents() {
        mMovieListFragment = addFragmentIfNotExists(childFragmentManager, R.id.flMovieListDefaultSortContainer, FAVORITE_MOVIE_LIST_FRAGMENT, {
            MovieListFragment.getInstance()
        })

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

    override fun showLoadingFavoriteMoviesIndicator() {
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
        private const val FAVORITE_MOVIE_LIST_FRAGMENT = "favorite_movie_list_fragment"

        fun getInstance(): FavoriteFragment = FavoriteFragment()
    }
}