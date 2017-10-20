package com.themovielist.injector.components

import com.themovielist.home.HomeFragment
import com.themovielist.home.list.HomeMovieListFragment
import com.themovielist.injector.PerFragment
import com.themovielist.moviedetail.MovieDetailFragment
import com.themovielist.moviedetail.review.MovieReviewListDialog
import com.themovielist.moviedetail.trailer.MovieTrailerListDialog
import com.themovielist.movielist.MovieListFragment

import dagger.Component

@PerFragment
@Component(dependencies = arrayOf(ApplicationComponent::class))
interface FragmentComponent {
    fun inject(fragment: MovieDetailFragment)
    fun inject(fragment: MovieListFragment)

    fun inject(fragment: HomeFragment)
    fun inject(fragment: HomeMovieListFragment)

    fun inject(dialogFragment: MovieReviewListDialog)
    fun inject(dialogFragment: MovieTrailerListDialog)
}
