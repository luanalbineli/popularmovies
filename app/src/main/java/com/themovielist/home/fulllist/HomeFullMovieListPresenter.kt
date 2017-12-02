package com.themovielist.home.fulllist

import android.util.SparseArray
import com.themovielist.enums.HomeMovieSortEnum
import com.themovielist.model.GenreModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.response.HomeFullMovieListResponseModel
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.model.view.HomeFullMovieListViewModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.repository.movie.MovieRepository
import com.themovielist.util.ApiUtil
import com.themovielist.util.Defaults
import com.themovielist.util.containsKey
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
            filterUpcomingMovieList(viewModel.movieList, viewModel.imageResponseModel!!, viewModel.genreMap, viewModel.selectedGenreMap, true)
            mView.scrollToItemPosition(viewModel.firstVisibleItemPosition)

            if (viewModel.hasMorePages) {
                mView.enableLoadMoreListener()
            } else {
                mView.disableLoadMoreListener()
            }

            mView.showGenreList(viewModel.genreMap.values()
                    .sortedBy { it.name }
                    .map {
                        GenreListItemModel(it, viewModel.selectedGenreMap.containsKey(it.id))
                    })

        } ?: fetchUpcomingMovieList()
    }

    private fun fetchUpcomingMovieList() {
        mView.showLoadingUpcomingMoviesIndicator()
        this.upcomingMoviesViewModel?.let {
            val request = if (it.filter == HomeMovieSortEnum.POPULAR)
                movieRepository.getMoviesByPopularityWithGenreAndConfiguration(it.pageIndex)
            else
                movieRepository.getMoviesByRatingWithGenreAndConfiguration(it.pageIndex)

            mRequest = request.doAfterTerminate {
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

    private fun handleSuccessfulUpcomingMoviesLoading(response: HomeFullMovieListResponseModel) {
        upcomingMoviesViewModel?.let {
            if (response.upcomingMovieList.results.isEmpty()) {
                if (it.pageIndex == ApiUtil.INITIAL_PAGE_INDEX) {
                    mView.showEmptyListMessage()
                }
            } else {
                buildMovieImageGenreViewModel(it, response)
            }

            Timber.d("handleSuccessfulUpcomingMoviesLoading - Have more pages: ${response.upcomingMovieList.hasMorePages()}")
            it.hasMorePages = response.upcomingMovieList.hasMorePages()
            if (response.upcomingMovieList.hasMorePages()) {
                mView.enableLoadMoreListener()
            } else {
                mView.disableLoadMoreListener()
            }

            if (it.pageIndex == ApiUtil.INITIAL_PAGE_INDEX) {
                mView.showGenreList(response.genreListModel.values()
                        .sortedBy { it.name }
                        .map {
                            GenreListItemModel(it, true)
                        })

                it.selectedGenreMap = response.genreListModel.clone()
                it.genreMap = response.genreListModel
            }
        }
    }

    private fun buildMovieImageGenreViewModel(viewModel: HomeFullMovieListViewModel, response: HomeFullMovieListResponseModel) {
        val finalMovieList = response.upcomingMovieList.results.map {
            val genreList = commonRepository.fillMovieGenresList(it.movieModel, response.genreListModel)
            MovieImageGenreViewModel(genreList, it.movieModel, response.favoriteMovieIds.contains(it.movieModel.id))
        }
        viewModel.movieList.addAll(finalMovieList)
        viewModel.imageResponseModel = response.configurationResponseModel.imageResponseModel
        filterUpcomingMovieList(finalMovieList, response.configurationResponseModel.imageResponseModel, viewModel.genreMap, viewModel.selectedGenreMap, false)
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

            filterUpcomingMovieList(it.movieList, it.imageResponseModel!!, it.genreMap, it.selectedGenreMap, true)
        }
    }

    private fun filterUpcomingMovieList(upcomingMovieList: List<MovieImageGenreViewModel>, imageResponseModel: ConfigurationImageResponseModel, genreMap: SparseArray<GenreModel>, selectedGenreMap: SparseArray<GenreModel>, replaceData: Boolean) {
        val finalUpcomingMovieList = if (genreMap.size() == selectedGenreMap.size()) {
            upcomingMovieList
        } else {
            upcomingMovieList.filter {
                // Always consider null genres
                it.genreList == null || it.genreList.any {
                    selectedGenreMap.containsKey(it.id)
                }
            }
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