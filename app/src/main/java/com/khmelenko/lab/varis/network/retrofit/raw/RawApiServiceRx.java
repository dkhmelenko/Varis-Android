package com.khmelenko.lab.varis.network.retrofit.raw;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;


/**
 * @author Dmytro Khmelenko
 */
public interface RawApiServiceRx {

    @GET("/jobs/{jobId}/log")
    Single<String> getLog(@Header("Authorization") String basicAuth, @Path("jobId") String jobId);

    @GET("/jobs/{jobId}/log")
    Single<String> getLog(@Path("jobId") String jobId);
}
