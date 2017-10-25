package com.themovielist.ui.movieimageview

import android.content.Context
import android.graphics.Paint
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.albineli.udacity.popularmovies.R
import com.like.LikeButton
import com.like.OnLikeListener
import com.themovielist.PopularMovieApplication
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieImageViewModel
import com.themovielist.model.MovieModel
import com.themovielist.moviedetail.MovieDetailActivity
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.movie_image_view.view.*
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

        tvMovieImageViewDetailButton.paintFlags = tvMovieImageViewDetailButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
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

    override fun toggleMovieFavorite(favorite: Boolean) {
        if (lbMovieImageViewFavorite.isLiked != favorite) {
            lbMovieImageViewFavorite.isLiked = favorite
        }
    }

    override fun showErrorFavoriteMovie(error: Throwable) {
        Timber.e(error, "An error occurred while tried to favorite the movie: ${error.message}")
        Snackbar.make(rootView, context.getString(R.string.error_add_favorite_movie), Snackbar.LENGTH_LONG)
    }

    override fun toggleMovieFavoriteEnabled(enabled: Boolean) {
        lbMovieImageViewFavorite.isEnabled = enabled
    }
}
