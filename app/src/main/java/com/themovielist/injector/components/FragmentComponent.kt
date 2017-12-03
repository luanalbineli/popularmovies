package com.themovielist.injector.components

import com.themovielist.browse.MovieBrowseFragment
import com.themovielist.favorite.FavoriteFragment
import com.themovielist.home.HomeFragment
import com.themovielist.home.fulllist.HomeFullMovieListActivity
import com.themovielist.home.partiallist.HomeMovieListFragment
import com.themovielist.injector.PerFragment
import com.themovielist.intheaters.InTheatersFragment
import com.themovielist.moviecast.MovieCastListFragment
import com.themovielist.moviedetail.MovieDetailActivity
import com.themovielist.moviedetail.review.MovieReviewListDialog
import com.themovielist.moviedetail.trailer.MovieTrailerListDialog
import com.themovielist.movielist.MovieListFragment
import com.themovielist.ui.movieimageview.MovieImageView
import dagger.Component

@PerFragment
@Component(dependencies = [(ApplicationComponent::class)])
interface FragmentComponent {
    fun inject(fragment: MovieDetailActivity)
    fun inject(fragment: FavoriteFragment)

    fun inject(fragment: HomeFragment)
    fun inject(fragment: HomeMovieListFragment)

    fun inject(fragment: InTheatersFragment)

    fun inject(dialogFragment: MovieReviewListDialog)
    fun inject(dialogFragment: MovieTrailerListDialog)
    fun inject(movieImageView: MovieImageView)

    fun inject(castListFragment: MovieCastListFragment)

    fun inject(movieBrowseFragment: MovieBrowseFragment)

    fun inject(movieListFragment: MovieListFragment)

    fun inject(fullMovieListActivity: HomeFullMovieListActivity)
}
