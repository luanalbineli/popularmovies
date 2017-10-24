package com.themovielist.base

import android.os.Bundle
import com.themovielist.PopularMovieApplication
import com.themovielist.injector.components.ApplicationComponent

abstract class BaseDaggerActivity<TView> : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val applicationComponent = PopularMovieApplication.getApplicationComponent(this)
        onInjectDependencies(applicationComponent)

        presenterImplementation.setView(viewImplementation)
    }

    protected abstract fun onInjectDependencies(applicationComponent: ApplicationComponent)

    protected abstract val presenterImplementation: BasePresenter<TView>
    protected abstract val viewImplementation: TView
}
