package com.themovielist.repository.data;


import android.database.Cursor;

import com.themovielist.model.MovieModel;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MovieDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MovieModel movieModel);

    @Query("SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME)
    Cursor selectAll();

    @Query("SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry._ID + " = :id")
    Cursor selectById(int id);

    @Query("DELETE FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry._ID + " = :id")
    int deleteById(int id);
}
