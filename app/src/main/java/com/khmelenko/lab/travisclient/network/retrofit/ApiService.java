package com.khmelenko.lab.travisclient.network.retrofit;

import com.khmelenko.lab.travisclient.network.dao.Repo;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Defines an interface for the REST service
 *
 * @author Dmytro Khmelenko
 */
public interface ApiService {

    @GET("/repos")
    public void getRepos(Callback<Repo> callback);

    @GET("/login/oauth/authorize")
    public void authorize(@Query("client_id") String clientId, Callback<String> callback);

    @POST("/login/oauth/access_token")
    public void accesToken(@Query("client_id") String clientId, @Query("client_secret") String clientSecret,
                           @Query("code") String code, Callback<String> callback);
}
