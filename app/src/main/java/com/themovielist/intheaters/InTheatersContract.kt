package com.themovielist.intheaters

import com.themovielist.base.BasePresenter
import com.themovielist.enums.MovieListFilterDescriptor
import com.themovielist.model.MovieListStateModel
import com.themovielist.model.MovieModel

interface InTheatersContract {
    interface View {
    }

    interface Presenter : BasePresenter<View> {
        fun start()
    }
}
