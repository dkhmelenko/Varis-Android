package com.khmelenko.lab.varis.network.retrofit.raw;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * @author Dmytro Khmelenko
 */
public interface RawApiService {

    @GET("/jobs/{jobId}/log")
    Response getLog(@Header("Authorization") String basicAuth, @Path("jobId") String jobId);

    @GET("/jobs/{jobId}/log")
    Response getLog(@Path("jobId") String jobId);
}
