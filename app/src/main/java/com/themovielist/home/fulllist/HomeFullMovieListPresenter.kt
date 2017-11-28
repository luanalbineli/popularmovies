package com.themovielist.home.fulllist

import android.util.SparseArray
import com.themovielist.model.GenreModel
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.response.HomeFullMovieListResponseModel
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.model.view.HomeFullMovieListViewModel
import com.themovielist.repository.movie.MovieRepository
import com.themovielist.util.ApiUtil
import com.themovielist.util.containsKey
import com.themovielist.util.values
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject


class HomeFullMovieListPresenter
@Inject
constructor(private var movieRepository: MovieRepository)
    : HomeFullMovieListContract.Presenter {

    private lateinit var mView: HomeFullMovieListContract.View

    override fun setView(view: HomeFullMovieListContract.View) {
        mView = view
    }

    var upcomingMoviesViewModel: HomeFullMovieListViewModel? = null

    private var mRequest: Disposable? = null

    private var mHasError = false

    override fun start(upcomingMoviesViewModel: HomeFullMovieListViewModel?) {
        this.upcomingMoviesViewModel = upcomingMoviesViewModel ?: HomeFullMovieListViewModel(ApiUtil.INITIAL_PAGE_INDEX)
        upcomingMoviesViewModel?.let { viewModel ->
            filterUpcomingMovieList(viewModel.upcomingMovieList, viewModel.imageResponseModel!!, viewModel.genreMap, viewModel.selectedGenreMap, true)
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
        mRequest = movieRepository.getUpcomingMoviesWithGenreAndConfiguration(upcomingMoviesViewModel!!.pageIndex)
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

    private fun handleSuccessfulUpcomingMoviesLoading(response: HomeFullMovieListResponseModel) {
        upcomingMoviesViewModel?.let {
            if (response.upcomingMovieList.results.isEmpty()) {
                if (it.pageIndex == ApiUtil.INITIAL_PAGE_INDEX) {
                    mView.showEmptyListMessage()
                }
            } else {
                it.upcomingMovieList.addAll(response.upcomingMovieList.results)
                it.imageResponseModel = response.configurationResponseModel.imageResponseModel
                filterUpcomingMovieList(response.upcomingMovieList.results, response.configurationResponseModel.imageResponseModel, it.genreMap, it.selectedGenreMap, false)
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

            filterUpcomingMovieList(it.upcomingMovieList, it.imageResponseModel!!, it.genreMap, it.selectedGenreMap, true)
        }
    }

    private fun filterUpcomingMovieList(upcomingMovieList: List<MovieWithGenreModel>, imageResponseModel: ConfigurationImageResponseModel, genreMap: SparseArray<GenreModel>, selectedGenreMap: SparseArray<GenreModel>, replaceData: Boolean) {
        val finalUpcomingMovieList = if (genreMap.size() == selectedGenreMap.size()) {
            upcomingMovieList
        } else {
            upcomingMovieList.filter {
                // Always consider null genres
                it.genreList == null || it.genreList!!.any {
                    selectedGenreMap.containsKey(it.id)
                }
            }
        }

        if (replaceData) {
            mView.replaceUpcomingMovieList(finalUpcomingMovieList, imageResponseModel)
        } else {
            mView.addUpcomingMovieList(finalUpcomingMovieList, imageResponseModel)
        }
    }

    override fun showMovieDetail(movieWithGenreModel: MovieWithGenreModel) {
        mView.showMovieDetail(movieWithGenreModel)
    }
}