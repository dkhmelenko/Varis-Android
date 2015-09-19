package com.khmelenko.lab.travisclient.network.retrofit;


import com.khmelenko.lab.travisclient.network.request.AccessTokenRequest;
import com.khmelenko.lab.travisclient.network.response.AccessToken;
import com.khmelenko.lab.travisclient.network.response.Branch;
import com.khmelenko.lab.travisclient.network.response.Branches;
import com.khmelenko.lab.travisclient.network.response.Logs;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.network.response.BuildHistory;
import com.khmelenko.lab.travisclient.network.response.Request;
import com.khmelenko.lab.travisclient.network.response.RequestData;
import com.khmelenko.lab.travisclient.network.response.Requests;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Defines an interface for the REST service
 *
 * @author Dmytro Khmelenko
 */
public interface TravisApiService {

    @POST("/auth/github")
    AccessToken auth(@Body AccessTokenRequest accessToken);

    // repositories
    @GET("/repos")
    List<Repo> getRepos();

    @GET("/repos")
    List<Repo> getRepos(@Query("search") String search);

    @GET("/repos/{repositoryId}")
    Repo getRepo(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}")
    Repo getRepo(@EncodedPath("repositorySlug") String repositorySlug);


    // branches
    @GET("/repos/{repositoryId}/branches")
    Branches getBranches(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}/branches")
    Branches getBranches(@EncodedPath("repositorySlug") String repositorySlug);

    @GET("/repos/{repositoryId}/branches/{branch}")
    Branch getBranch(@Path("repositoryId") long repositoryId, @Path("branch") String branch);

    @GET("/repos/{repositorySlug}/branches/{branch}")
    Branch getBranch(@EncodedPath("repositorySlug") String repositorySlug, @Path("branch") String branch);


    // builds
    @GET("/repos/{repositoryId}/builds")
    BuildHistory getBuilds(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}/builds")
    BuildHistory getBuilds(@EncodedPath("repositorySlug") String repositorySlug);

    @GET("/repos/{repositoryId}/builds?event_type=pull_request")
    BuildHistory getPullRequestBuilds(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}/builds?event_type=pull_request")
    BuildHistory getPullRequestBuilds(@EncodedPath("repositorySlug") String repositorySlug);

    @GET("/repos/{repositoryId}/builds/{buildId}")
    BuildHistory getBuild(@Path("repositoryId") long repositoryId, @Path("buildId") long buildId);

    @GET("/repos/{repositorySlug}/builds/{buildId}")
    BuildHistory getBuild(@EncodedPath("repositorySlug") String repositorySlug, @Path("buildId") long buildId);

    @POST("/builds/{buildId}/cancel")
    void cancelBuild(@Path("buildId") long buildId);

    @POST("/builds/{buildId}/restart")
    void restartBuild(@Path("buildId") long buildId);


    // requests
    @GET("/requests/{requestId}")
    Request getRequest(@Path("requestId") long requestId);

    @GET("/requests")
    Requests getRequests(@Query("repository_id") long repositoryId);

    @GET("/requests")
    Requests getRequests(@Query("slug") String repositorySlug);


    // logs
    @GET("/jobs/{jobId}/log")
    String getLogRaw(@Path("jobId") long jobId);

    @GET("/logs/{logId}")
    Logs getLogs(@Path("logId") long logId);
}
