package com.khmelenko.lab.varis.network.retrofit.raw

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


/**
 * @author Dmytro Khmelenko
 */
interface RawApiService {

    @GET("/jobs/{jobId}/log")
    fun getLog(@Header("Authorization") basicAuth: String, @Path("jobId") jobId: String): Single<String>

    @GET("/jobs/{jobId}/log")
    fun getLog(@Path("jobId") jobId: String): Single<String>
}
