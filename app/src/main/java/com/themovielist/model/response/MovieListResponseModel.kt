package com.themovielist.model.response

import android.util.SparseArray
import com.themovielist.model.GenreModel
import com.themovielist.model.MovieWithGenreModel
import java.util.*


data class MovieListResponseModel(val configurationResponseModel: ConfigurationResponseModel,
                                  var movieWithGenreList: PaginatedArrayResponseModel<MovieWithGenreModel>,
                                  var genreListModel: SparseArray<GenreModel>,
                                  val favoriteMovieIds: Array<Int>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieListResponseModel

        if (configurationResponseModel != other.configurationResponseModel) return false
        if (movieWithGenreList != other.movieWithGenreList) return false
        if (genreListModel != other.genreListModel) return false
        if (!Arrays.equals(favoriteMovieIds, other.favoriteMovieIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = configurationResponseModel.hashCode()
        result = 31 * result + movieWithGenreList.hashCode()
        result = 31 * result + genreListModel.hashCode()
        result = 31 * result + Arrays.hashCode(favoriteMovieIds)
        return result
    }
}