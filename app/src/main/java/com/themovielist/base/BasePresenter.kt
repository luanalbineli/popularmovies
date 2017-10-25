package com.themovielist.base


interface BasePresenter<in TView> {

    fun setView(view: TView)
}
