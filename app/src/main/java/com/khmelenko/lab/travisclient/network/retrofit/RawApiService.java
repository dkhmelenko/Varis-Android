package com.khmelenko.lab.travisclient.network.retrofit;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * @author Dmytro Khmelenko
 */
public interface RawApiService {

    @GET("/jobs/{jobId}/log")
    void getLog(@Header("Authorization") String basicAuth, @Path("jobId") String jobId, Callback<String> response);
}
