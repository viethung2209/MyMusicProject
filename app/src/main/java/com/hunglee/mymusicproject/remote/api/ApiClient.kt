package com.hunglee.mymusicproject.remote.api

import android.util.Log
import android.webkit.CookieManager
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {
    private const val BASE_URL = "https://mp3.zing.vn/xhr/"//"https://m.zingmp3.vn/api/"
    private var zingMp3Service: ZingMp3Service? = null

    @JvmStatic
    fun getApiService(baseUrl: String): ZingMp3Service {
        if (zingMp3Service == null) {
            val okHttpClient = buildHttpClient()
            zingMp3Service = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ZingMp3Service::class.java)
        }
        return zingMp3Service!!
    }

    private fun buildHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .callTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .cookieJar(buildCoolieJar())
            .addInterceptor(buildLoggingInterceptor())
            .build()
    }

    private fun buildCoolieJar(): CookieJar {
        return object : CookieJar {
            val cookieManager = CookieManager.getInstance()

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies: MutableList<Cookie> = ArrayList()
                if (cookieManager.getCookie(url.toString()) != null) {
                    val splitCookies: List<String> =
                        cookieManager.getCookie(url.toString()).split("[,;]")
                    for (i in splitCookies.indices) {
                        cookies.add(Cookie.parse(url, splitCookies[i].trim { it <= ' ' })!!)
                    }
                }
                return cookies
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                for (cookie in cookies) {
                    cookieManager.setCookie(url.toString(), cookie.toString())
                }
            }
        }
    }

    private fun buildLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor.Builder()
            .setLevel(Level.BASIC)
            .log(Log.VERBOSE)
            .build()
    }
}