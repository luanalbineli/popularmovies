package com.themovielist


import android.app.Application
import android.content.ContentResolver
import android.content.Context

import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerApplicationComponent
import com.themovielist.injector.modules.ApplicationModule
import com.facebook.drawee.backends.pipeline.Fresco
import com.squareup.leakcanary.LeakCanary
import com.themovielist.model.MovieModel
import com.themovielist.repository.ArrayRequestAPI
import io.reactivex.ObservableEmitter

import timber.log.Timber

class PopularMovieApplication : Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        // Timber
        Timber.plant(Timber.DebugTree())
        // Fresco
        Fresco.initialize(this)
        // LeakCanary
        LeakCanary.install(this)
        // Dagger2
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    inline fun <T> safeContentResolver(emitter: ObservableEmitter<T>, safeFunction: ContentResolver.() -> Unit) {
        val contentResolver = contentResolver
        if (contentResolver == null) {
            emitter.onError(RuntimeException("Cannot get the ContentResolver"))
            return
        }

        safeFunction.invoke(contentResolver)
    }

    companion object {

        fun getApplicationComponent(context: Context): ApplicationComponent {
            return (context.applicationContext as PopularMovieApplication).applicationComponent
        }
    }
}
