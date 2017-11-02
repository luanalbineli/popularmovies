package com.themovielist.ui.movieimageview

import android.content.Context
import android.net.Uri
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.albineli.udacity.popularmovies.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.themovielist.PopularMovieApplication
import com.themovielist.event.FavoriteMovieEvent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieImageViewModel
import com.themovielist.model.MovieModel
import com.themovielist.moviedetail.MovieDetailActivity
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
        LayoutInflater.from(context).inflate(R.layout.movie_image_view, this)

        mfbMovieImageViewFavorite.setOnFavoriteChangeListener { _, _ ->
            if (mIsFavoritingManually) {
                return@setOnFavoriteChangeListener
            }
            mPresenter.toggleMovieFavorite()
        }

        sdvMovieImageView.setOnClickListener { mPresenter.showMovieDetail() }
    }

    private fun injectDependencies() {
        val applicationComponent = PopularMovieApplication.getApplicationComponent(context)

        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)

        mPresenter.setView(this)
    }

    fun setImageURI(posterUrl: String) {
        val posterWidth = context.resources.getDimensionPixelSize(R.dimen.home_movie_list_image_width)
        val posterHeight = context.resources.getDimensionPixelSize(R.dimen.home_movie_list_image_height)

        val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(posterUrl))
                .setResizeOptions(ResizeOptions(posterWidth, posterHeight))
                .build()

        sdvMovieImageView.controller = Fresco.newDraweeControllerBuilder()
                .setOldController(sdvMovieImageView.controller)
                .setImageRequest(request)
                .build()
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
        toggleMovieFavorite(movieImageViewModel.isFavourite)
    }
}
