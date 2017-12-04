package com.themovielist.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arlib.floatingsearchview.FloatingSearchView
import com.themovielist.R
import com.themovielist.base.BaseFragment
import com.themovielist.base.BasePresenter
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieCastViewModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.model.view.MovieSuggestionModel
import com.themovielist.movielist.MovieListFragment
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.movie_browse_fragment.*
import javax.inject.Inject

class MovieBrowseFragment : BaseFragment<MovieBrowseContract.View>(), MovieBrowseContract.View {
    override val presenterImplementation: BasePresenter<MovieBrowseContract.View>
        get() = mPresenter
    override val viewImplementation: MovieBrowseContract.View
        get() = this

    @Inject
    lateinit var mPresenter: MovieBrowsePresenter

    lateinit var mMovieListFragment: MovieListFragment

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

        configureComponents()

        val movieCastViewModel = buildMovieCastViewModel(savedInstanceState)
        mPresenter.start(movieCastViewModel)
    }

    private fun configureComponents() {
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

        mMovieListFragment = fragmentManager.findFragmentById(R.id.fragmentBrowseMovieList) as? MovieListFragment ?:
                childFragmentManager.findFragmentById(R.id.fragmentBrowseMovieList) as MovieListFragment

        mMovieListFragment.useListLayout()
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

    override fun closeSuggestion() {
        fsvMovieBrowseSearch.closeMenu(false)
    }

    override fun showMovieList(movieList: List<MovieImageGenreViewModel>, configurationImageResponseModel: ConfigurationImageResponseModel) {
        blah.setDisplay(false)
        flMovieListContainer.setDisplay(true)
        mMovieListFragment.replaceMoviesToList(movieList, configurationImageResponseModel)
    }

    override fun showErrorLoadingMovieCast(error: Throwable) {
        mMovieListFragment.showErrorLoadingMovies()
    }

    override fun hideLoadingIndicator() {
        mMovieListFragment.hideLoadingIndicator()
    }

    override fun showLoadingIndicator() {
        mMovieListFragment.showLoadingIndicator()
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
