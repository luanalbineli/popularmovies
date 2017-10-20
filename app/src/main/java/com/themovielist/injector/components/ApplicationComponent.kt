package com.themovielist.injector.components


import com.themovielist.PopularMovieApplication
import com.themovielist.injector.modules.ApplicationModule

import javax.inject.Singleton

import dagger.Component
import retrofit2.Retrofit

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun retrofit(): Retrofit
    fun popularMovieApplicationContext(): PopularMovieApplication
}
