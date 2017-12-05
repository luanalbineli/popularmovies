package com.themovielist.intheaters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.themovielist.R
import com.themovielist.base.BaseFragment
import com.themovielist.base.BasePresenter
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.movielist.MovieListFragment
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.movie_header_detail.*
import timber.log.Timber
import javax.inject.Inject


class InTheatersFragment : BaseFragment<InTheatersContract.View>(), InTheatersContract.View {
    override val presenterImplementation: BasePresenter<InTheatersContract.View>
        get() = mPresenter

    override val viewImplementation: InTheatersContract.View
        get() = this

    @Inject
    lateinit var mPresenter: InTheatersPresenter

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.in_theaters_fragment, container, false)
    }

    private lateinit var mMovieListFragment: MovieListFragment

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setTitle(R.string.cinema)

        mMovieListFragment = fragmentManager.findFragmentById(R.id.fragmentMovieList) as? MovieListFragment ?:
                childFragmentManager.findFragmentById(R.id.fragmentMovieList) as MovieListFragment

        mMovieListFragment.useListLayout()

        mPresenter.start()
    }

    override fun showMainMovieDetail(movieWithGenreModel: MovieWithGenreModel) {
        val posterWidth = ApiUtil.getDefaultPosterSize(sdvMovieHeaderBackdrop.width)
        if (movieWithGenreModel.movieModel.posterPath != null) {
            val posterUrl = ApiUtil.buildPosterImageUrl(movieWithGenreModel.movieModel.posterPath!!, posterWidth)

            sdvMovieHeaderBackdrop.setImageURI(posterUrl)
        }


        tvMovieHeaderReleaseDate.text = "1h 20m" // TODO: TEST
        tvMovieHeaderMovieName.text = movieWithGenreModel.movieModel.title
        tvMovieHeaderMovieGenres.text = movieWithGenreModel.genreList?.map { it.name }?.reduce { a, b -> "$a, $b"} ?: ""
    }

    override fun showMovieList(results: List<MovieImageGenreViewModel>, configurationResponseModel: ConfigurationImageResponseModel) {
        mMovieListFragment.addMoviesToList(results, configurationResponseModel)
    }

    override fun showLoadingIndicator() {
        mMovieListFragment.showLoadingIndicator()
    }

    override fun hideLoadingIndicator() {
        mMovieListFragment.hideLoadingIndicator()
    }

    override fun showErrorLoadingMovies(error: Throwable) {
        Timber.e(error, "An error occurred while tried to fetch the in theaters movies: ${error.message}")
        mMovieListFragment.showErrorLoadingMovies()
    }

    companion object {
        fun getInstance(): InTheatersFragment = InTheatersFragment()
    }
}
