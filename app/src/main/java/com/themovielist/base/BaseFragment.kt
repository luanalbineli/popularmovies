package com.themovielist.base

import android.app.Fragment
import android.os.Bundle

import com.themovielist.PopularMovieApplication
import com.themovielist.injector.components.ApplicationComponent

abstract class BaseFragment<TView> : Fragment(), CommonMethods {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val applicationComponent = PopularMovieApplication.getApplicationComponent(activity)
        onInjectDependencies(applicationComponent)

        presenterImplementation.setView(viewImplementation)
    }

    protected abstract fun onInjectDependencies(applicationComponent: ApplicationComponent)

    protected abstract val presenterImplementation: BasePresenter<TView>
    protected abstract val viewImplementation: TView
}
