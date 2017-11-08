package com.themovielist.moviecast

import com.themovielist.base.BasePresenter
import com.themovielist.model.view.MovieCastViewModel

interface MovieCastContract {
    interface View {
        fun showLoadingIndicator()
    }

    interface Presenter : BasePresenter<View> {
        fun start(movieCastViewModel: MovieCastViewModel)
    }
}