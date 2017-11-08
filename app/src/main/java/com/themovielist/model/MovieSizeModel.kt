package com.themovielist.model

import com.themovielist.enums.ImageSizeEnum

data class MovieSizeModel constructor(val size: Int, @ImageSizeEnum.ImageSize val sizeType: Int)