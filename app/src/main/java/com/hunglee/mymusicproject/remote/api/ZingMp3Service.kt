package com.hunglee.mymusicproject.remote.api

import com.hunglee.mymusicproject.remote.response.chart.ChartResponse
import com.hunglee.mymusicproject.util.API_KEY
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ZingMp3Service {

    @GET("chart-realtime/get-detail") //
    fun getChart(
        @Query("type") type: String,
        @Query("time") time: Int,
        @Query("count") count: Int,
        @Query("ctime") ctime: String,
        @Query("sig") sig: String,
        @Query("api_key") apiKey: String = API_KEY
    ): Observable<ChartResponse>

    @GET("recommend")
    fun getRecommend(
        @Query("id") id: String,
        @Query("ctime") ctime: String,
        @Query("sig") sig: String,
        @Query("api_key") apiKey: String = API_KEY
    ): Observable<ChartResponse>

    @GET("song/get-song-info")
    fun getSongInformation(
        @Query("id") id: String,
        @Query("ctime") ctime: String,
        @Query("sig") sig: String,
        @Query("api_key") apiKey: String = API_KEY
    )

    @GET("song/get-lyric")
    fun getSongLyric(
        @Query("id") id: String,
        @Query("ctime") ctime: String,
        @Query("sig") sig: String,
        @Query("api_key") apiKey: String = API_KEY
    )

    @GET("{type}/get-streamings")
    fun getStreamings(
        @Path("type") type: String,
        @Query("id") id: String,
        @Query("ctime") ctime: String,
        @Query("sig") sig: String,
        @Query("api_key") apiKey: String = API_KEY
    )

    @GET("playlist/get-playlist-detail")
    fun getPlaylist(
        @Query("id") id: String,
        @Query("ctime") ctime: String,
        @Query("sig") sig: String,
        @Query("api_key") apiKey: String = API_KEY
    )

}