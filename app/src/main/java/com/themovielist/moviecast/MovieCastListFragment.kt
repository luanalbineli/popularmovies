package com.themovielist.moviecast

import android.os.Bundle
import android.view.View
import com.themovielist.base.BasePresenter
import com.themovielist.base.BaseRecyclerViewFragment
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.view.MovieCastViewModel
import javax.inject.Inject

class MovieCastListFragment : BaseRecyclerViewFragment<MovieCastContract.View>(), MovieCastContract.View {
    override val presenterImplementation: BasePresenter<MovieCastContract.View>
        get() = mPresenter
    override val viewImplementation: MovieCastContract.View
        get() = this

    @Inject
    lateinit var mPresenter: MovieCastPresenter

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieCastViewModel = buildMovieCastViewModel(savedInstanceState)

        mPresenter.start(movieCastViewModel)
    }

    private fun buildMovieCastViewModel(savedInstanceState: Bundle?): MovieCastViewModel {
        return if (savedInstanceState == null) {
            MovieCastViewModel()
        } else {
            savedInstanceState.getParcelable(MOVIE_CAST_VIEW_MODEL_BUNDLE_KEY)
        }
    }

    override fun showLoadingIndicator() {
    }

    companion object {
        const val MOVIE_CAST_VIEW_MODEL_BUNDLE_KEY = "movie_cast_view_model"
        const val MOVIE_ID_BUNDLE_KEY = "movie_id"

        fun getInstance(movieId: Int): MovieCastListFragment {
            val instance = MovieCastListFragment()
            instance.arguments = Bundle().also {
                it.putInt(MOVIE_ID_BUNDLE_KEY, movieId)
            }
            return instance
        }
    }
}
