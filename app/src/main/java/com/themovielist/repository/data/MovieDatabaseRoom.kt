package com.themovielist.repository.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

import com.themovielist.model.MovieModel

@Database(entities = [(MovieModel::class)], version = 1)
@TypeConverters(RoomConverters::class)
abstract class MovieDatabaseRoom : RoomDatabase() {

    abstract fun movieDAO(): MovieDAO

    companion object {
        private const val DATABASE_NAME = "movies.db"
        private var mInstance: MovieDatabaseRoom? = null

        @Synchronized
        fun getInstance(context: Context): MovieDatabaseRoom {
            if (mInstance == null) {
                synchronized(MovieDatabaseRoom::class) {
                    mInstance = Room.databaseBuilder(context.applicationContext,
                            MovieDatabaseRoom::class.java, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build()
                }
            }

            return mInstance!!
        }
    }


}
