package com.themovielist.ui.movieimageview

import android.content.Context
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.albineli.udacity.popularmovies.R
import com.like.LikeButton
import com.like.OnLikeListener
import com.themovielist.PopularMovieApplication
import com.themovielist.event.FavoriteMovieEvent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieImageViewModel
import com.themovielist.model.MovieModel
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

    init {
        injectDependencies()

        LayoutInflater.from(context).inflate(R.layout.movie_image_view, this)
        sdvMovieImageView.setOnClickListener {
            mPresenter.openMenu()
        }

        clMovieImageViewMenuContainer.setOnClickListener {
            mPresenter.closeMenu()
        }

        tvMovieImageViewDetailButton.setOnClickListener {
            mPresenter.showMovieDetail()
        }

        lbMovieImageViewFavorite.setOnLikeListener(object: OnLikeListener { // This is not fired when set the isLiked property manually. \o/
            override fun liked(p0: LikeButton?) {
                mPresenter.toggleMovieFavorite()
            }

            override fun unLiked(p0: LikeButton?) {
                mPresenter.toggleMovieFavorite()
            }
        })
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
        sdvMovieImageView.setImageURI(posterUrl)
    }

    fun setMovieImageViewModel(movieImageViewModel: MovieImageViewModel) {
        mPresenter.setMovieImageViewModel(movieImageViewModel)
    }

    override fun openMovieDetail(movieModel: MovieModel) {
        val intent = MovieDetailActivity.getDefaultIntent(context, movieModel)
        context.startActivity(intent)
    }

    override fun toggleMenuOpened(opened: Boolean) {
        clMovieImageViewMenuContainer.setDisplay(opened)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBus.getDefault().register(this)
        Timber.d("The view attached to window")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBus.getDefault().unregister(this)
        Timber.d("The view detached from window")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFavoriteMovieEvent(favoriteMovieEvent: FavoriteMovieEvent) {
        Timber.d("Reached the favorite movie event: $favoriteMovieEvent")
        mPresenter.onFavoriteMovieEvent(favoriteMovieEvent.movie, favoriteMovieEvent.favorite)
    }

    override fun toggleMovieFavorite(favourite: Boolean) {
        lbMovieImageViewFavorite.isLiked = favourite
    }

    override fun showErrorFavoriteMovie(error: Throwable) {
        Timber.e(error, "An error occurred while tried to favorite the movie: ${error.message}")
        Snackbar.make(rootView, context.getString(R.string.error_add_favorite_movie), Snackbar.LENGTH_LONG)
    }

    override fun toggleMovieFavouriteEnabled(enabled: Boolean) {
        lbMovieImageViewFavorite.isEnabled = enabled
    }

    override fun showMovieInfo(movieImageViewModel: MovieImageViewModel) {
        tvMovieImageViewName.text = movieImageViewModel.movieModel.title
        toggleMenuOpened(movieImageViewModel.isMenuOpen)
        toggleMovieFavorite(movieImageViewModel.isFavourite)
    }
}
