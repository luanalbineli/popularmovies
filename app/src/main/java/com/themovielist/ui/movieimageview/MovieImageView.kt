package com.themovielist.ui.movieimageview

import android.content.Context
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.albineli.udacity.popularmovies.R
import com.themovielist.PopularMovieApplication
import com.themovielist.event.FavoriteMovieEvent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieModel
import com.themovielist.model.view.MovieImageViewModel
import com.themovielist.moviedetail.MovieDetailActivity
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.movie_image_view.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject


class MovieImageView constructor(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet), MovieImageViewContract.View {

    @Inject
    lateinit var mPresenter: MovieImageViewPresenter

    private var mIsFavoritingManually = false

    init {
        injectDependencies()

        /*
        * For some F3cking unknown reason, I was unable to change the SimpleDraweeView width/height/aspectRatio on runtime, turning this component useless.
        * I had to pass the layout by parameter (changing the width/height/aspectRatio of the SimpleDraweeView on the XML. Sad :/
        * */
        val simpleDraweeViewLayout = getSimpleDraweeView(attributeSet)

        LayoutInflater.from(context).inflate(simpleDraweeViewLayout, this)

        mfbMovieImageViewFavorite.setOnFavoriteChangeListener { _, _ ->
            if (mIsFavoritingManually) {
                return@setOnFavoriteChangeListener
            }
            mPresenter.toggleMovieFavorite()
        }

        sdvMovieImageView.setOnClickListener { mPresenter.showMovieDetail() }
    }

    private fun getSimpleDraweeView(attributeSet: AttributeSet): Int {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MovieImageView)
        val layoutResId = typedArray.getResourceId(R.styleable.MovieImageView_simpleDraweeView, R.layout.movie_image_view)
        typedArray.recycle()

        return layoutResId
    }

    private fun injectDependencies() {
        val applicationComponent = PopularMovieApplication.getApplicationComponent(context)

        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)

        mPresenter.setView(this)
    }

    fun setImageURI(posterUrl: String?) {
        sdvMovieImageView.setImageURI(posterUrl)
        ivMovieItemEmptyImage.setDisplay(posterUrl == null)
    }

    fun setMovieImageViewModel(movieImageViewModel: MovieImageViewModel) {
        mPresenter.setMovieImageViewModel(movieImageViewModel)
    }

    override fun openMovieDetail(movieModel: MovieModel) {
        val intent = MovieDetailActivity.getDefaultIntent(context, movieModel)
        context.startActivity(intent)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBus.getDefault().register(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFavoriteMovieEvent(favoriteMovieEvent: FavoriteMovieEvent) {
        mPresenter.onFavoriteMovieEvent(favoriteMovieEvent.movie, favoriteMovieEvent.favorite)
    }

    override fun toggleMovieFavorite(favourite: Boolean) {
        mIsFavoritingManually = true
        mfbMovieImageViewFavorite.isFavorite = favourite
        mIsFavoritingManually = false
    }

    override fun showErrorFavoriteMovie(error: Throwable) {
        Timber.e(error, "An error occurred while tried to favorite the movie: ${error.message}")
        Snackbar.make(rootView, context.getString(R.string.error_add_favorite_movie), Snackbar.LENGTH_LONG)
    }

    override fun toggleMovieFavouriteEnabled(enabled: Boolean) {
        mfbMovieImageViewFavorite.isEnabled = enabled
    }

    override fun showMovieInfo(movieImageViewModel: MovieImageViewModel) {
        tvMovieImageViewName.text = movieImageViewModel.movieModel.title
        toggleMovieFavorite(movieImageViewModel.isFavorite)
    }
}
