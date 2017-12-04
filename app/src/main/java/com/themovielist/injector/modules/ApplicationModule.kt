package com.themovielist.injector.modules

import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
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
import java.lang.reflect.Type
import java.util.*
import javax.inject.Singleton
import timber.log.Timber
import java.text.SimpleDateFormat


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
        val gsonBuilder = GsonBuilder()

        gsonBuilder.registerTypeAdapter(Date::class.java, CustomEmptyDateDeserializer())

        return GsonConverterFactory.create(gsonBuilder.create())
    }

    // TODO: REVIEW IT
    private class CustomEmptyDateDeserializer: JsonDeserializer<Date> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date? {
            if (json == null) {
                return null
            }

            return try {
                if (TextUtils.isEmpty(json.asString)) {
                    null
                } else {
                    return dateFormat.parse(json.asString)
                }
            } catch (exception: Exception) {
                Timber.e(exception, "An error occurred while tried to parse the date")
                null
            }
        }

    }

    companion object {
        private val BASE_URL = "https://api.themoviedb.org/3/"
    }
}
