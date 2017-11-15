package com.themovielist.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.arlib.floatingsearchview.FloatingSearchView
import com.themovielist.base.BasePresenter
import com.themovielist.base.BaseRecyclerViewFragment
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieCastModel
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieSizeModel
import com.themovielist.model.view.MovieCastViewModel
import com.themovielist.model.view.MovieSuggestionModel
import kotlinx.android.synthetic.main.movie_browse_fragment.*
import timber.log.Timber
import javax.inject.Inject

class MovieBrowseFragment : BaseRecyclerViewFragment<MovieBrowseContract.View>(), MovieBrowseContract.View {
    override val presenterImplementation: BasePresenter<MovieBrowseContract.View>
        get() = mPresenter
    override val viewImplementation: MovieBrowseContract.View
        get() = this

    private val mAdapter by lazy { MovieBrowseAdapter(R.string.the_list_is_empty, { mPresenter.tryAgain() }) }

    @Inject
    lateinit var mPresenter: MovieBrowsePresenter

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

        rvRecyclerView.adapter = mAdapter

        useLinearLayoutManager()

        fsvMovieBrowseSearch.setOnQueryChangeListener { _, newQuery ->
            mPresenter.onQueryChanged(newQuery)
        }

        fsvMovieBrowseSearch.setOnBindSuggestionCallback { suggestionView, _, _, searchSuggestion, _ ->
            suggestionView.setOnClickListener {
                val movieSuggestionModel = searchSuggestion as MovieSuggestionModel
                mPresenter.onSelectSuggestion(movieSuggestionModel)
            }
        }

        fsvMovieBrowseSearch.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocusCleared() {
                fsvMovieBrowseSearch.setSearchHint(getString(R.string.search))
            }

            override fun onFocus() {
                fsvMovieBrowseSearch.setSearchHint(getString(R.string.type_at_least_three_characters))
            }
        })

        val movieCastViewModel = buildMovieCastViewModel(savedInstanceState)
        mPresenter.start(movieCastViewModel)
    }

    override fun showLoadingQueryResultIndicator() {
        fsvMovieBrowseSearch.showProgress()
    }

    override fun showSuggestion(suggestionList: List<MovieModel>) {
        fsvMovieBrowseSearch.swapSuggestions(suggestionList.map { MovieSuggestionModel(it) })
    }

    override fun hideLoadingQueryResultIndicator() {
        fsvMovieBrowseSearch.hideProgress()
    }

    private fun buildMovieCastViewModel(savedInstanceState: Bundle?): MovieCastViewModel {
        return if (savedInstanceState == null) {
            MovieCastViewModel()
        } else {
            savedInstanceState.getParcelable(MOVIE_CAST_VIEW_MODEL_BUNDLE_KEY)
        }
    }

    override fun showErrorLoadingMovieCast(error: Throwable) {
        Timber.e(error, "An error occurred while tried to fetch the movie cast: ${error.message}")
        mAdapter.showErrorMessage()
    }

    override fun showMovieCastList(movieCastList: List<MovieCastModel>, profileSizeList: List<MovieSizeModel>) {
        mAdapter.addItems(movieCastList)
    }

    override fun hideLoadingIndicator() {
        mAdapter.hideRequestStatus()
    }

    override fun showLoadingIndicator() {
        mAdapter.showLoading()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    companion object {
        const val MOVIE_CAST_VIEW_MODEL_BUNDLE_KEY = "movie_cast_view_model"
        fun getInstance(): MovieBrowseFragment {
            return MovieBrowseFragment()
        }
    }
}
