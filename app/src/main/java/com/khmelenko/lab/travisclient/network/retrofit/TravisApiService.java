package com.khmelenko.lab.travisclient.network.retrofit;


import com.khmelenko.lab.travisclient.network.request.AccessTokenRequest;
import com.khmelenko.lab.travisclient.network.response.AccessToken;
import com.khmelenko.lab.travisclient.network.response.Branch;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.network.response.Request;

import java.util.List;

import retrofit.http.Body;
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

    @Headers({
            "Accept: application/vnd.travis-ci.2+json",
            "User-Agent:TravisClient/1.0.0"
    })
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
    Repo getRepo(@Path("repositorySlug") String repositorySlug);


    // branches
    @GET("/repos/{repositoryId}/branches")
    List<Branch> getBranches(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}/branches")
    List<Branch> getBranches(@Path("repositorySlug") String repositorySlug);

    @GET("/repos/{repositorySlug}/branches/{branch}")
    Branch getBranch(@Path("repositoryId") long repositoryId, @Path("branch") String branch);

    @GET("/repos/{repositorySlug}/branches/{branch}")
    Branch getBranch(@Path("repositorySlug") String repositorySlug, @Path("branch") String branch);


    // builds
    @GET("/repos/{repositoryId}/builds")
    List<Build> getBuilds(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}/builds")
    List<Build> getBuilds(@Path("repositorySlug") String repositorySlug);

    @GET("/repos/{repositoryId}/builds/{buildId}")
    List<Build> getBuild(@Path("repositoryId") long repositoryId, @Path("buildId") long buildId);

    @GET("/repos/{repositorySlug}/builds/{buildId}")
    List<Build> getBuild(@Path("repositorySlug") String repositorySlug, @Path("buildId") long buildId);

    @POST("/builds/{buildId}/cancel")
    void cancelBuild(@Path("buildId") long buildId);

    @POST("/builds/{buildId}/restart")
    void restartBuild(@Path("buildId") long buildId);


    // requests
    @GET("/requests/{requestId}")
    List<Request> getRequest(@Path("requestId") long requestId);

    @GET("/requests")
    List<Request> getRequests(@Query("repository_id") long repositoryId);

    @GET("/requests")
    List<Request> getRequests(@Query("slug") String repositorySlug);

}
