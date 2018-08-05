package com.khmelenko.lab.varis.network.retrofit.travis

import android.text.TextUtils

import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.util.PackageUtils

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * REST client for Network communication
 *
 * @author Dmytro Khmelenko
 */
class TravisRestClient(
        private val retrofit: Retrofit,
        private val okHttpClient: OkHttpClient,
        private val appSettings: AppSettings
) {

    /**
     * Gets travis API service
     *
     * @return Travis API service
     */
    lateinit var apiService: TravisApiService
        private set

    init {
        val travisUrl = appSettings.serverUrl
        updateTravisEndpoint(travisUrl)
    }

    /**
     * Updates Travis endpoint
     *
     * @param newEndpoint New endpoint
     */
    fun updateTravisEndpoint(newEndpoint: String) {
        val retrofit = retrofit.newBuilder()
                .baseUrl(newEndpoint)
                .client(httpClient())
                .build()

        apiService = retrofit.create(TravisApiService::class.java)
    }

    private fun httpClient(): OkHttpClient {
        val userAgent = String.format("TravisClient/%1\$s", PackageUtils.appVersion)

        return okHttpClient.newBuilder()
                .addInterceptor { chain ->
                    val original = chain.request()

                    val request = original.newBuilder()
                            .header("User-Agent", userAgent)
                            .header("Accept", "application/vnd.travis-ci.2+json")

                    val accessToken = appSettings.accessToken
                    if (!TextUtils.isEmpty(accessToken)) {
                        val headerValue = String.format("token %1\$s", accessToken)
                        request.addHeader("Authorization", headerValue)
                    }

                    chain.proceed(request.build())
                }
                .build()
    }
}
