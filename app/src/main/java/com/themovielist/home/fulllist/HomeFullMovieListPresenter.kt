package com.themovielist.home.fulllist

import com.themovielist.enums.HomeMovieSortEnum
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.response.MovieListResponseModel
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.model.view.HomeFullMovieListViewModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.repository.movie.MovieRepository
import com.themovielist.util.ApiUtil
import com.themovielist.util.Defaults
import com.themovielist.util.values
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject


class HomeFullMovieListPresenter
@Inject
constructor(private var movieRepository: MovieRepository, private var commonRepository: CommonRepository)
    : HomeFullMovieListContract.Presenter {

    private lateinit var mView: HomeFullMovieListContract.View

    override fun setView(view: HomeFullMovieListContract.View) {
        mView = view
    }

    var upcomingMoviesViewModel: HomeFullMovieListViewModel? = null

    private var mRequest: Disposable? = null

    private var mHasError = false

    override fun start(upcomingMoviesViewModel: HomeFullMovieListViewModel?, filter: Int) {
        this.upcomingMoviesViewModel = upcomingMoviesViewModel ?: HomeFullMovieListViewModel(filter, ApiUtil.INITIAL_PAGE_INDEX)

        mView.setTitleByFilter(filter)

        val useListViewType = commonRepository.getUseListViewType(Defaults.USE_LIST_VIEW_TYPE)
        mView.setListViewType(useListViewType)

        upcomingMoviesViewModel?.let { viewModel ->
            filterUpcomingMovieList(viewModel.movieList, viewModel.imageResponseModel!!, viewModel.genreListItemModelList, true)
            mView.scrollToItemPosition(viewModel.firstVisibleItemPosition)

            if (viewModel.hasMorePages) {
                mView.enableLoadMoreListener()
            } else {
                mView.disableLoadMoreListener()
            }

            mView.showGenreList(viewModel.genreListItemModelList)

        } ?: fetchUpcomingMovieList()
    }

    private fun fetchUpcomingMovieList() {
        mView.showLoadingUpcomingMoviesIndicator()
        this.upcomingMoviesViewModel?.let {
            val request = if (it.filter == HomeMovieSortEnum.POPULAR)
                movieRepository.getMoviesByPopularityWithGenreAndConfiguration(it.pageIndex)
            else
                movieRepository.getMoviesByRatingWithGenreAndConfiguration(it.pageIndex)

            mRequest = request
                    .doAfterTerminate {
                        mView.hideLoadingIndicator()
                    }
                    .subscribe({ response ->
                        mRequest = null
                        handleSuccessfulUpcomingMoviesLoading(response)
                    }, { error ->
                        mRequest = null
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

            // If is the first page, fill the genre filter list.
            if (it.pageIndex == ApiUtil.INITIAL_PAGE_INDEX) {
                val genreListItemModel = response.genreListModel.values()
                        .sortedBy { it.name }
                        .map {
                            GenreListItemModel(it, false)
                        }
                mView.showGenreList(genreListItemModel)

                it.genreListItemModelList = genreListItemModel
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
        filterUpcomingMovieList(finalMovieList, response.configurationResponseModel.imageResponseModel, viewModel.genreListItemModelList, false)
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
        Timber.d("loadMoreMovies - Called")
        // If mHasError, wait for for the user, to click the try again button
        if (mHasError || mRequest != null) {
            return
        }


        upcomingMoviesViewModel?.let {
            it.pageIndex++
        }
        Timber.d("loadMoreMovies - Fetching the page: ${upcomingMoviesViewModel?.pageIndex}")
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
            filterUpcomingMovieList(it.movieList, it.imageResponseModel!!, it.genreListItemModelList, true)
        }
    }

    private fun filterUpcomingMovieList(upcomingMovieList: List<MovieImageGenreViewModel>, imageResponseModel: ConfigurationImageResponseModel, selectedGenreMap: List<GenreListItemModel>, replaceData: Boolean) {
        val selectedGenreList = selectedGenreMap.filter { it.selected }
        Timber.d("filterUpcomingMovieList - selectedGenreList: ${selectedGenreList.isEmpty()}")
        val finalUpcomingMovieList = if (selectedGenreList.isEmpty()) {
            upcomingMovieList
        } else {
            upcomingMovieList.filter { movie ->
                // Always consider null genres
                movie.genreList == null || movie.genreList.any { movieGenreModel ->
                    selectedGenreList.any { it.genreModel.id == movieGenreModel.id }
                }
            }
        }

        if (selectedGenreList.isNotEmpty() && finalUpcomingMovieList.isEmpty()) {
            Timber.d("filterUpcomingMovieList - Loading more moves, because the result list is empty")
            loadMoreMovies()
        }

        if (replaceData) {
            mView.replaceMovieList(finalUpcomingMovieList, imageResponseModel)
        } else {
            mView.addMoviesToList(finalUpcomingMovieList, imageResponseModel)
        }
    }

    override fun showMovieDetail(movieImageGenreViewModel: MovieImageGenreViewModel) {
        mView.showMovieDetail(movieImageGenreViewModel)
    }

    override fun onChangeListViewType(useListViewType: Boolean) {
        commonRepository.putUseListViewType(useListViewType)
    }
}