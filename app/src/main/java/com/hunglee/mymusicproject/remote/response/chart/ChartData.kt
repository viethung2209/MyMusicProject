package com.hunglee.mymusicproject.remote.response.chart

import com.hunglee.mymusicproject.model.Song

data class ChartData(
    var items: MutableList<Song>
)