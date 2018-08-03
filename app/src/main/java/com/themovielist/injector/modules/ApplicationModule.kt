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
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
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
        val gson = GsonBuilder()
                // Handle empty release_date cases.
                .registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date> {
                    var dateFormat: DateFormat = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault())
                    @Throws(JsonParseException::class)
                    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date? {
                        return try {
                            if (json.asString.isNullOrEmpty())
                                null
                            else
                                dateFormat.parse(json.asString)
                        } catch (e: ParseException) {
                            null
                        }

                    }
                })
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
