package com.themovielist.favorite

import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.response.MovieListResponseModel
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.model.view.HomeFullMovieListViewModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.repository.movie.MovieRepository
import com.themovielist.util.ApiUtil
import com.themovielist.util.Defaults
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject


class FavoritePresenter
@Inject
constructor(private var movieRepository: MovieRepository, private var commonRepository: CommonRepository)
    : FavoriteContract.Presenter {

    private lateinit var mView: FavoriteContract.View

    override fun setView(view: FavoriteContract.View) {
        mView = view
    }

    var upcomingMoviesViewModel: HomeFullMovieListViewModel? = null

    private var mRequest: Disposable? = null

    private var mHasError = false

    override fun start(upcomingMoviesViewModel: HomeFullMovieListViewModel?) {
        this.upcomingMoviesViewModel = upcomingMoviesViewModel ?: HomeFullMovieListViewModel(Defaults.FAVORITE_LIST_SORT, ApiUtil.INITIAL_PAGE_INDEX)

        val useListViewType = commonRepository.getUseListViewType(Defaults.USE_LIST_VIEW_TYPE)
        mView.setListViewType(useListViewType)

        upcomingMoviesViewModel?.let { viewModel ->
            filterUpcomingMovieList(viewModel.movieList, viewModel.imageResponseModel!!, true)
            mView.scrollToItemPosition(viewModel.firstVisibleItemPosition)

            if (viewModel.hasMorePages) {
                mView.enableLoadMoreListener()
            } else {
                mView.disableLoadMoreListener()
            }

        } ?: fetchUpcomingMovieList()
    }

    private fun fetchUpcomingMovieList() {
        mView.showLoadingFavoriteMoviesIndicator()

        this.upcomingMoviesViewModel?.let {
            mRequest = movieRepository.getFavoriteMovieListWithGenreAndConfiguration()
                    .doAfterTerminate {
                        mView.hideLoadingIndicator()
                        mRequest = null
                    }
                    .subscribe({ response ->
                        handleSuccessfulUpcomingMoviesLoading(response)
                    }, { error ->
                        handleErrorLoadingUpcomingMovies(error)
                    })
        }

    }

    private fun handleSuccessfulUpcomingMoviesLoading(response: MovieListResponseModel) {
        upcomingMoviesViewModel?.let {
            if (response.movieWithGenreList.results.isEmpty()) {
                if (it.pageIndex == ApiUtil.INITIAL_PAGE_INDEX) {
                    mView.showEmptyListMessage()
                }
            } else {
                buildMovieImageGenreViewModel(it, response)
            }

            Timber.d("handleSuccessfulUpcomingMoviesLoading - Have more pages: ${response.movieWithGenreList.hasMorePages()}")
            it.hasMorePages = response.movieWithGenreList.hasMorePages()
            if (response.movieWithGenreList.hasMorePages()) {
                mView.enableLoadMoreListener()
            } else {
                mView.disableLoadMoreListener()
            }

            if (it.pageIndex == ApiUtil.INITIAL_PAGE_INDEX) {
                it.selectedGenreMap = response.genreListModel.clone()
                it.genreMap = response.genreListModel
            }
        }
    }

    private fun buildMovieImageGenreViewModel(viewModel: HomeFullMovieListViewModel, response: MovieListResponseModel) {
        val finalMovieList = response.movieWithGenreList.results.map {
            val genreList = commonRepository.fillMovieGenresList(it.movieModel, response.genreListModel)
            MovieImageGenreViewModel(genreList, it.movieModel, response.favoriteMovieIds.contains(it.movieModel.id))
        }
        viewModel.movieList.addAll(finalMovieList)
        viewModel.imageResponseModel = response.configurationResponseModel.imageResponseModel
        filterUpcomingMovieList(finalMovieList, response.configurationResponseModel.imageResponseModel, false)
    }

    private fun handleErrorLoadingUpcomingMovies(error: Throwable) {
        mHasError = true
        mView.showErrorLoadingUpcomingMovies(error)
        upcomingMoviesViewModel?.let {
            if (it.pageIndex > ApiUtil.INITIAL_PAGE_INDEX) {
                it.pageIndex--
            }
        }

    }

    override fun tryAgain() {
        mHasError = false
        fetchUpcomingMovieList()
    }

    override fun loadMoreMovies() {
        // If mHasError, wait for for the user, to click the try again button
        if (mHasError || mRequest != null) {
            return
        }

        upcomingMoviesViewModel?.let {
            it.pageIndex++
        }
        fetchUpcomingMovieList()
    }

    override fun onStop(firstVisibleItemPosition: Int) {
        upcomingMoviesViewModel?.firstVisibleItemPosition = firstVisibleItemPosition
        mRequest?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

    override fun onChangeSelectedGenre(genreListItemModel: GenreListItemModel) {
        upcomingMoviesViewModel?.let {
            if (genreListItemModel.selected) {
                it.selectedGenreMap.append(genreListItemModel.genreModel.id, genreListItemModel.genreModel)
            } else {
                it.selectedGenreMap.delete(genreListItemModel.genreModel.id)
            }

            filterUpcomingMovieList(it.movieList, it.imageResponseModel!!, true)
        }
    }

    private fun filterUpcomingMovieList(favoriteMovieList: List<MovieImageGenreViewModel>, imageResponseModel: ConfigurationImageResponseModel, replaceData: Boolean) {
        if (replaceData) {
            mView.replaceMovieList(favoriteMovieList, imageResponseModel)
        } else {
            mView.addMoviesToList(favoriteMovieList, imageResponseModel)
        }
    }

    override fun showMovieDetail(movieImageGenreViewModel: MovieImageGenreViewModel) {
        mView.showMovieDetail(movieImageGenreViewModel)
    }

    override fun onChangeListViewType(useListViewType: Boolean) {
        commonRepository.putUseListViewType(useListViewType)
    }

    override fun handleSortMenuClick() {
        mView.openDialogToSelectListSort(0)
    }

    fun onChangeListSort(index: Int) {

    }
}