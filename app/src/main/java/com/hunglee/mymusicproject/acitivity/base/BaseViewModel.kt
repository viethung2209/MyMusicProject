package com.hunglee.mymusicproject.acitivity.base

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.hieubui00.musicplayer.util.ApiHelper
import com.hunglee.mymusicproject.remote.response.BaseResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

open class BaseViewModel {
//    protected val zingMp3Service: ZingMp3Service = ApiClient.getApiService()

    @SuppressLint("StaticFieldLeak")
    protected fun <D, T : BaseResponse<D>> getData(
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