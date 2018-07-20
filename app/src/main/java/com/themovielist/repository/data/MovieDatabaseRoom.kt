package com.themovielist.repository.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
