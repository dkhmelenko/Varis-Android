package com.khmelenko.lab.travisclient.network.retrofit;

import com.khmelenko.lab.travisclient.network.request.AuthorizationRequest;
import com.khmelenko.lab.travisclient.network.response.Authorization;
import com.khmelenko.lab.travisclient.network.response.GithubAccessToken;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Header;
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

    @POST("/authorization")
    Authorization createNewAuthorization(@Header("Authorization") String authorization,
                                  @Body AuthorizationRequest authorizationRequest);

    @DELETE("/authorization")
    void deleteAuthorization(@Header("Authorization") String authorization);
}
