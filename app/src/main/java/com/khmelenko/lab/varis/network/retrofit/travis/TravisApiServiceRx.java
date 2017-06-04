package com.khmelenko.lab.varis.network.retrofit.travis;

import com.khmelenko.lab.varis.network.request.AccessTokenRequest;
import com.khmelenko.lab.varis.network.response.AccessToken;
import com.khmelenko.lab.varis.network.response.Branch;
import com.khmelenko.lab.varis.network.response.Branches;
import com.khmelenko.lab.varis.network.response.BuildDetails;
import com.khmelenko.lab.varis.network.response.BuildHistory;
import com.khmelenko.lab.varis.network.response.Logs;
import com.khmelenko.lab.varis.network.response.Repo;
import com.khmelenko.lab.varis.network.response.Request;
import com.khmelenko.lab.varis.network.response.Requests;
import com.khmelenko.lab.varis.network.response.User;

import java.util.List;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface TravisApiServiceRx {

    @POST("/auth/github")
    Single<AccessToken> auth(@Body AccessTokenRequest accessToken);

    // repositories
    @GET("/repos")
    Single<List<Repo>> getRepos();

    @GET("/repos")
    Single<List<Repo>> getRepos(@Query("search") String search);

    @GET("/repos/{repositoryId}")
    Single<Repo> getRepo(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}")
    Single<Repo> getRepo(@Path(value = "repositorySlug", encoded = true) String repositorySlug);

    @GET("/repos")
    Single<List<Repo>> getUserRepos(@Query("member") String userName);


    // branches
    @GET("/repos/{repositoryId}/branches")
    Single<Branches> getBranches(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}/branches")
    Single<Branches> getBranches(@Path(value = "repositorySlug", encoded = true) String repositorySlug);

    @GET("/repos/{repositoryId}/branches/{branch}")
    Single<Branch> getBranch(@Path("repositoryId") long repositoryId, @Path("branch") String branch);

    @GET("/repos/{repositorySlug}/branches/{branch}")
    Single<Branch> getBranch(@Path(value = "repositorySlug", encoded = true) String repositorySlug, @Path("branch") String branch);


    // builds
    @GET("/repos/{repositoryId}/builds")
    Single<BuildHistory> getBuilds(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}/builds")
    Single<BuildHistory> getBuilds(@Path(value = "repositorySlug", encoded = true) String repositorySlug);

    @GET("/repos/{repositoryId}/builds?event_type=pull_request")
    Single<BuildHistory> getPullRequestBuilds(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}/builds?event_type=pull_request")
    Single<BuildHistory> getPullRequestBuilds(@Path(value = "repositorySlug", encoded = true) String repositorySlug);

    @GET("/repos/{repositoryId}/builds/{buildId}")
    Single<BuildDetails> getBuild(@Path("repositoryId") long repositoryId, @Path("buildId") long buildId);

    @GET("/repos/{repositorySlug}/builds/{buildId}")
    Single<BuildDetails> getBuild(@Path(value = "repositorySlug", encoded = true) String repositorySlug, @Path("buildId") long buildId);

    @POST("/builds/{buildId}/cancel")
    Single<Object> cancelBuild(@Path("buildId") long buildId, @Body RequestBody emptyBody);

    @POST("/builds/{buildId}/restart")
    Single<Object> restartBuild(@Path("buildId") long buildId, @Body RequestBody emptyBody);


    // requests
    @GET("/requests/{requestId}")
    Single<Request> getRequest(@Path("requestId") long requestId);

    @GET("/requests")
    Single<Requests> getRequests(@Query("repository_id") long repositoryId);

    @GET("/requests")
    Single<Requests> getRequests(@Query("slug") String repositorySlug);


    // logs
    @GET("/jobs/{jobId}/log")
    Single<String> getLogRaw(@Path("jobId") long jobId);

    @GET("/logs/{logId}")
    Single<Logs> getLogs(@Path("logId") long logId);


    // users
    @GET("/users")
    Single<User> getUser();
}
