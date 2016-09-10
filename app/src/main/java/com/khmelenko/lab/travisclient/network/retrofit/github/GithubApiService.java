package com.khmelenko.lab.travisclient.network.retrofit.github;

import com.khmelenko.lab.travisclient.network.request.AuthorizationRequest;
import com.khmelenko.lab.travisclient.network.response.Authorization;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Defines API for working with Github
 *
 * @author Dmytro Khmelenko
 */
public interface GithubApiService {

    public static final String TWO_FACTOR_HEADER = "X-GitHub-OTP";

    @POST("/authorizations")
    Authorization createNewAuthorization(@Header("Authorization") String basicAuth,
                                         @Body AuthorizationRequest authorizationRequest);

    @POST("/authorizations")
    Authorization createNewAuthorization(@Header("Authorization") String basicAuth,
                                         @Header(TWO_FACTOR_HEADER) String twoFactorCode,
                                         @Body AuthorizationRequest authorizationRequest);

    @DELETE("/authorizations/{authorizationId}")
    Object deleteAuthorization(@Header("Authorization") String basicAuth,
                               @Path("authorizationId") String authorizationId);

    @DELETE("/authorizations/{authorizationId}")
    Object deleteAuthorization(@Header("Authorization") String basicAuth,
                               @Header(TWO_FACTOR_HEADER) String twoFactorCode,
                               @Path("authorizationId") String authorizationId);
}
