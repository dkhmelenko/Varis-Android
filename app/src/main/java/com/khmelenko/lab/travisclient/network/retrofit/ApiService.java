package com.khmelenko.lab.travisclient.network.retrofit;

import com.khmelenko.lab.travisclient.network.dao.Repo;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Defines an interface for the REST service
 *
 * @author Dmytro Khmelenko
 */
public interface ApiService {

    @GET("/repos")
    public void getRepos(Callback<Repo> callback);
}
