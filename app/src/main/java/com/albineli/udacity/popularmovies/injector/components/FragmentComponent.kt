package com.albineli.udacity.popularmovies.injector.components

import com.albineli.udacity.popularmovies.injector.PerFragment
import com.albineli.udacity.popularmovies.moviedetail.MovieDetailFragment
import com.albineli.udacity.popularmovies.moviedetail.review.MovieReviewListDialog
import com.albineli.udacity.popularmovies.moviedetail.trailer.MovieTrailerListDialog
import com.albineli.udacity.popularmovies.movielist.MovieListFragment

import dagger.Component

@PerFragment
@Component(dependencies = arrayOf(ApplicationComponent::class))
interface FragmentComponent {
    fun inject(fragment: MovieDetailFragment)
    fun inject(fragment: MovieListFragment)

    fun inject(dialogFragment: MovieReviewListDialog)
    fun inject(dialogFragment: MovieTrailerListDialog)
}
