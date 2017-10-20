package com.themovielist.injector.components

import com.themovielist.mainactivity.MainActivity
import com.themovielist.injector.PerActivity

import dagger.Component

@PerActivity
@Component(dependencies = arrayOf(ApplicationComponent::class))
interface ActivityComponent {
    fun inject(mainActivity: MainActivity)
}
