package com.khmelenko.lab.travisclient.network.retrofit;

import com.khmelenko.lab.travisclient.network.dao.Branch;
import com.khmelenko.lab.travisclient.network.dao.Repo;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Defines an interface for the REST service
 *
 * @author Dmytro Khmelenko
 */
public interface TravisApiService {

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
