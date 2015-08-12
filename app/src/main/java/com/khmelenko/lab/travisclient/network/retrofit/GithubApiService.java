package com.khmelenko.lab.travisclient.network.retrofit;

import com.khmelenko.lab.travisclient.network.response.GithubAccessToken;

import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Defines API for working with Github
 *
 * @author Dmytro Khmelenko
 */
public interface GithubApiService {

    @Headers("Accept: application/json")
    @POST("/login/oauth/access_token")
    GithubAccessToken getAccesToken(@Query("client_id") String clientId, @Query("client_secret") String clientSecret,
                                    @Query("code") String code);
}
