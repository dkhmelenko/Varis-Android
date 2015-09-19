package com.khmelenko.lab.travisclient.network.retrofit;

import com.khmelenko.lab.travisclient.network.request.AuthorizationRequest;
import com.khmelenko.lab.travisclient.network.response.Authorization;
import com.khmelenko.lab.travisclient.network.response.GithubAccessToken;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Defines API for working with Github
 *
 * @author Dmytro Khmelenko
 */
public interface GithubApiService {

    @POST("/authorizations")
    Authorization createNewAuthorization(@Header("Authorization") String basicAuth,
                                  @Body AuthorizationRequest authorizationRequest);

    @DELETE("/authorizations/{authorizationId}")
    Object deleteAuthorization(@Header("Authorization") String basicAuth,
                             @Path("authorizationId") String authorizationId);
}
