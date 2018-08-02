package com.khmelenko.lab.varis.network.retrofit.raw

import com.khmelenko.lab.varis.storage.AppSettings

import java.lang.reflect.Type

import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


/**
 * Raw http client
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class RawClient(private var retrofit: Retrofit, private val httpClient: OkHttpClient, appSettings: AppSettings) {

    /**
     * Gets Raw API service
     *
     * @return Raw API service
     */
    lateinit var apiService: RawApiService
        private set

    init {
        val travisUrl = appSettings.serverUrl
        updateEndpoint(travisUrl)
    }

    /**
     * Updates Travis endpoint
     *
     * @param newEndpoint New endpoint
     */
    fun updateEndpoint(newEndpoint: String) {
        retrofit = Retrofit.Builder()
                .baseUrl(newEndpoint)
                .addConverterFactory(object : Converter.Factory() {
                    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
                        return if (String::class.java == type) {
                            Converter<ResponseBody, String> { value -> value.string() }
                        } else null
                    }
                })
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
        apiService = retrofit.create(RawApiService::class.java)
    }

    /**
     * Executes single request
     *
     * @param url URL for request
     * @return Response
     */
    fun singleRequest(url: String): Single<Response> {

        val request = Request.Builder()
                .url(url)
                .build()

        return Single.create { e ->
            val response = httpClient.newCall(request).execute()
            e.onSuccess(response)
        }
    }

    /**
     * Executes single request
     *
     * @param url URL for request
     * @return String
     */
    fun singleStringRequest(url: String): Single<String> {

        val request = Request.Builder()
                .url(url)
                .build()

        return Single.create { e ->
            val response = httpClient.newCall(request).execute()
            e.onSuccess(response.body()!!.string())
        }
    }

    fun getLogUrl(jobId: Long?): String {
        return String.format("%sjobs/%d/log", retrofit.baseUrl(), jobId)
    }
}
