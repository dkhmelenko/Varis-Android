package com.khmelenko.lab.travisclient.network.retrofit;


import com.khmelenko.lab.travisclient.network.request.AccessTokenRequest;
import com.khmelenko.lab.travisclient.network.response.AccessToken;
import com.khmelenko.lab.travisclient.network.response.Branch;
import com.khmelenko.lab.travisclient.network.response.Repo;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

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

    @GET("/repos")
    List<Repo> getRepos();

    @GET("/repos/{repositoryId}")
    List<Repo> getRepo(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}")
    List<Repo> getRepo(@Path("repositorySlug") String repositorySlug);

    @GET("/repos/{repositoryId}/branches")
    List<Branch> getBranches(@Path("repositoryId") long repositoryId);

    @GET("/repos/{repositorySlug}/branches")
    List<Branch> getBranches(@Path("repositorySlug") String repositorySlug);

}
