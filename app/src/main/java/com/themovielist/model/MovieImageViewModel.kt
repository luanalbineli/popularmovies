package com.themovielist.model

data class MovieImageViewModel(val movieModel: MovieModel, var isFavorite: Boolean, var isMenuOpen: Boolean = false)