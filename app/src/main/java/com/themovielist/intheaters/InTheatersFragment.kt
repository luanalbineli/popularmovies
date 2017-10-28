package com.themovielist.intheaters

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.base.BaseFragment
import com.themovielist.base.BasePresenter
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.in_theaters_fragment.*
import timber.log.Timber
import javax.inject.Inject


class InTheatersFragment : BaseFragment<InTheatersContract.View>(), InTheatersContract.View {
    override val presenterImplementation: BasePresenter<InTheatersContract.View>
        get() = mPresenter

    override val viewImplementation: InTheatersContract.View
        get() = this

    @Inject
    lateinit var mPresenter: InTheatersPresenter

    private val mMovieListAdapter by lazy { InTheatersAdapter(R.string.the_list_is_empty, { /*mPresenter.tryAgain()*/ })}

    private val mGridLayoutManager by lazy { GridLayoutManager(activity, 2) }

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.in_theaters_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setTitle(R.string.cinema)

        mPresenter.start()
    }

    override fun showMainMovieDetail(movieWithGenreModel: MovieWithGenreModel) {
        val posterWidth = ApiUtil.getDefaultPosterSize(sdvMovieDetailBackdrop.width)
        val posterUrl = ApiUtil.buildPosterImageUrl(movieWithGenreModel.posterPath, posterWidth)

        sdvMovieDetailBackdrop.setImageURI(posterUrl)

        tvInTheatersMovieDuration.text = "1h 20m" // TODO: TEST
        tvInTheatersMovieName.text = movieWithGenreModel.title
        tvInTheatersMovieGenres.text = movieWithGenreModel.genreList?.map { it.name }?.reduce {a, b -> "$a, $b"} ?: ""
    }

    override fun showMovieList(results: List<MovieWithGenreModel>) {

    }

    override fun showErrorLoadingMovies(error: Throwable) {
        Timber.e(error, "An error occurred while tried to fetch the in theaters movies: ${error.message}")
        // TODO: Handle error.
    }

    companion object {
        fun getInstance(): InTheatersFragment {
            return InTheatersFragment()
        }
    }
}
