package com.themovielist.model.response

import android.util.SparseArray
import com.themovielist.model.GenreModel
import com.themovielist.model.MovieWithGenreModel


data class HomeFullMovieListResponseModel(val configurationResponseModel: ConfigurationResponseModel,
                                          var upcomingMovieList: PaginatedArrayResponseModel<MovieWithGenreModel>,
                                          var genreListModel: SparseArray<GenreModel>)