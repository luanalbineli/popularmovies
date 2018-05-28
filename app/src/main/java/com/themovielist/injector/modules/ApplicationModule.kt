package com.themovielist.injector.modules

import com.google.gson.GsonBuilder
import com.themovielist.BuildConfig
import com.themovielist.PopularMovieApplication
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Singleton


@Module
class ApplicationModule(private val mPopularMovieApplication: PopularMovieApplication) {

    @Provides
    @Singleton
    fun providePopularMovieApplicationContext(): PopularMovieApplication {
        return mPopularMovieApplication
    }

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        // Add a log interceptor
        val httpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
        }

        httpClient.addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val urlBuilder = chain.request().url().newBuilder()
            urlBuilder.addQueryParameter("api_key", BuildConfig.API_KEY)
            urlBuilder.addQueryParameter("region", Locale.getDefault().country)
            requestBuilder.url(urlBuilder.build())
            chain.proceed(requestBuilder.build())
        }

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(buildGsonConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(httpClient.build())
                .build()
    }

    private fun buildGsonConverter(): Converter.Factory {
        // https://github.com/google/gson/issues/1096
        val gson = GsonBuilder()
                .setDateFormat(DEFAULT_DATE_FORMAT)
                .create()

        val dateTypeAdapter = gson.getAdapter(Date::class.java)

        val safeDateTypeAdapter = dateTypeAdapter.nullSafe()

        val gsonBuilder = GsonBuilder()
                .setDateFormat(DEFAULT_DATE_FORMAT)
                .registerTypeAdapter(Date::class.java, safeDateTypeAdapter)

        return GsonConverterFactory.create(gsonBuilder.create())
    }

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"
        private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    }
}
