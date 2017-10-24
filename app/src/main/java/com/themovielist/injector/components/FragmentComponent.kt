package com.themovielist.injector.components

import com.themovielist.home.HomeFragment
import com.themovielist.home.list.HomeMovieListFragment
import com.themovielist.injector.PerFragment
import com.themovielist.intheaters.InTheatersFragment
import com.themovielist.moviedetail.MovieDetailActivity
import com.themovielist.moviedetail.review.MovieReviewListDialog
import com.themovielist.moviedetail.trailer.MovieTrailerListDialog
import com.themovielist.movielist.MovieListFragment

import dagger.Component

@PerFragment
@Component(dependencies = arrayOf(ApplicationComponent::class))
interface FragmentComponent {
    fun inject(fragment: MovieDetailActivity)
    fun inject(fragment: MovieListFragment)

    fun inject(fragment: HomeFragment)
    fun inject(fragment: HomeMovieListFragment)

    fun inject(fragment: InTheatersFragment)

    fun inject(dialogFragment: MovieReviewListDialog)
    fun inject(dialogFragment: MovieTrailerListDialog)
}
