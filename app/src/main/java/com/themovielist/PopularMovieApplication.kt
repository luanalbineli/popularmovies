package com.themovielist


import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import com.facebook.drawee.backends.pipeline.Fresco
import com.squareup.leakcanary.LeakCanary
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerApplicationComponent
import com.themovielist.injector.modules.ApplicationModule
import com.themovielist.util.tryExecute
import io.reactivex.ObservableEmitter
import timber.log.Timber
import java.sql.SQLDataException
import com.facebook.common.logging.FLog
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.listener.RequestLoggingListener
import com.facebook.imagepipeline.listener.RequestListener



class PopularMovieApplication : Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        // Fresco.
        val requestListeners = HashSet<RequestListener>()
        requestListeners.add(RequestLoggingListener())
        val config = ImagePipelineConfig.newBuilder(this)
                .setRequestListeners(requestListeners)
                .build()
        Fresco.initialize(this, config)
        FLog.setMinimumLoggingLevel(FLog.VERBOSE)

        // Timber
        Timber.plant(Timber.DebugTree())
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

    inline fun <T> tryQueryOnContentResolver(emitter: ObservableEmitter<T>, cursorInvoker: ContentResolver.() -> Cursor?, safeFunction: Cursor.() -> Unit) {
        val contentResolver = contentResolver
        if (contentResolver == null) {
            emitter.onError(RuntimeException("Cannot get the ContentResolver"))
            return
        }

        val cursor = cursorInvoker.invoke(contentResolver)
        if (cursor == null) {
            emitter.onError(SQLDataException("An internal error occurred."))
            return
        }

        cursor.tryExecute(emitter, safeFunction)
    }

    companion object {

        fun getApplicationComponent(context: Context): ApplicationComponent {
            return (context.applicationContext as PopularMovieApplication).applicationComponent
        }
    }
}
