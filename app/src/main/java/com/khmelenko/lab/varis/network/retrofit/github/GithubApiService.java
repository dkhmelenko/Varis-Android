package com.khmelenko.lab.varis.network.retrofit.github;

import com.khmelenko.lab.varis.network.request.AuthorizationRequest;
import com.khmelenko.lab.varis.network.response.Authorization;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Defines API for working with Github
 *
 * @author Dmytro Khmelenko
 */
public interface GithubApiService {

    String TWO_FACTOR_HEADER = "X-GitHub-OTP";

    @POST("/authorizations")
    Single<Authorization> createNewAuthorization(@Header("Authorization") String basicAuth,
                                                @Body AuthorizationRequest authorizationRequest);

    @POST("/authorizations")
    Single<Authorization> createNewAuthorization(@Header("Authorization") String basicAuth,
                                         @Header(TWO_FACTOR_HEADER) String twoFactorCode,
                                         @Body AuthorizationRequest authorizationRequest);

    @DELETE("/authorizations/{authorizationId}")
    Single<Object> deleteAuthorization(@Header("Authorization") String basicAuth,
                               @Path("authorizationId") String authorizationId);

    @DELETE("/authorizations/{authorizationId}")
    Single<Object> deleteAuthorization(@Header("Authorization") String basicAuth,
                               @Header(TWO_FACTOR_HEADER) String twoFactorCode,
                               @Path("authorizationId") String authorizationId);
}
