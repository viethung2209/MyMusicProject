package com.hunglee.mymusicproject.acitivity.ui.home

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hieubui00.musicplayer.util.ApiHelper
import com.hunglee.mymusicproject.model.Song
import com.hunglee.mymusicproject.remote.api.ApiClient
import com.hunglee.mymusicproject.remote.api.ZingMp3Service
import com.hunglee.mymusicproject.remote.response.BaseResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

class HomeViewModel : ViewModel() {
    private val zingMp3ServiceC: ZingMp3Service = ApiClient.getApiService("https://m.zingmp3.vn/api/")
    private val zingMp3ServiceR: ZingMp3Service = ApiClient.getApiService("https://mp3.zing.vn/xhr/")
    val songCharts: MutableLiveData<MutableList<Song>?> = MutableLiveData()
    val recommenedSong: MutableLiveData<MutableList<Song>?> = MutableLiveData()
    val randomID = Random.nextInt(0, 99)

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is Home Fragment"
//
//    }
//    val text: LiveData<String> = _text

    fun getRecommendedSong() {
        getData(
            "/recommend",
            mutableMapOf("id" to "ZZIDIODE"),
            { params ->
                zingMp3ServiceR.getRecommend(
                    params["id"]!!.toString(),
                    params["ctime"]!!,
                    params["sig"]!!
                )
            },
            { result ->
                recommenedSong.value = result.data?.items
            },
            { throwable ->
                throwable.printStackTrace()
            }
        )
    }

    fun getSongCharts() {

        getData(
            "/chart-realtime/get-detail",//
            mutableMapOf("type" to "song", "time" to "-1", "count" to "100"),
            { params ->
                zingMp3ServiceC.getChart(
                    params["type"]!!,
                    params["time"]!!.toInt(),
                    params["count"]!!.toInt(),
                    params["ctime"]!!,
                    params["sig"]!!,
                )
            },
            { response ->
                songCharts.value = response.data?.items
            },
            { throwable ->
                throwable.printStackTrace()
            },
        )

    }

    @SuppressLint("StaticFieldLeak")
    fun <D, T : BaseResponse<D>> getData(
        api: String,
        params: MutableMap<String, String>,
        getObservable: (params: MutableMap<String, String>) -> Observable<T>,
        onSuccess: (result: BaseResponse<D>) -> Unit,
        onError: (throwable: Throwable) -> Unit
    ) {
        object : AsyncTask<MutableMap<String, String>, Unit, MutableMap<String, String>>() {

            override fun doInBackground(vararg params: MutableMap<String, String>): MutableMap<String, String> {
                val ctime = ApiHelper.getCTime()
                params[0]["ctime"] = ctime
                val sig = ApiHelper.getSig(api, params[0])
                params[0]["sig"] = sig
                return params[0]
            }

            @SuppressLint("CheckResult")
            override fun onPostExecute(result: MutableMap<String, String>) {
                val observable = getObservable(result)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                callApi(observable, onSuccess, onError)
            }
        }.execute(params)
    }

    private fun <D, T : BaseResponse<D>> callApi(
        observable: Observable<T>,
        onSuccess: (result: BaseResponse<D>) -> Unit,
        onError: (throwable: Throwable) -> Unit
    ): Disposable {
        return observable.subscribe(
            { response ->
                if (response.err != -201) {
                    onSuccess(response)
                } else {
                    callApi(observable, onSuccess, onError)
                }
            },
            { throwable ->
                onError(throwable)
            }
        )
    }
}

