package com.hunglee.mymusicproject.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Song(
    var id: String,

    var title: String,

    @SerializedName("artists_names")
    var artistsNames: String,

    var thumbnail: String?,

    @SerializedName("thumbnail_medium")
    var thumbnailMedium: String?,

    var lyric: String?,

    var listen: Int,

    var duration: Int,

    var path: String ,

    var fileName: String = "",

    var album: String = "",

    var isLiked: Boolean = false
) : Serializable