package com.khmelenko.lab.varis.network.retrofit.github;

import com.khmelenko.lab.varis.network.request.AuthorizationRequest;
import com.khmelenko.lab.varis.network.response.Authorization;

import io.reactivex.Single;
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
public interface GithubApiServiceRx {

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
