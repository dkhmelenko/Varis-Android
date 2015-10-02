package com.khmelenko.lab.travisclient.network.retrofit;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.khmelenko.lab.travisclient.util.PackageUtils;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * REST client for Network communication
 *
 * @author Dmytro Khmelenko
 */
public final class RestClient {
    private static final String TRAVIS_URL = Constants.OPEN_SOURCE_TRAVIS_URL;
    private static final String GITHUB_URL = Constants.GITHUB_URL;

    private TravisApiService mApiService;
    private GithubApiService mGithubApiService;

    public RestClient() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                String userAgent = String.format("TravisClient/%1$s", PackageUtils.getAppVersion());
                request.addHeader("User-Agent", userAgent);
                request.addHeader("Accept", "application/vnd.travis-ci.2+json");

                String accessToken = AppSettings.getAccessToken();
                if(!TextUtils.isEmpty(accessToken)) {
                    String headerValue = String.format("token %1$s", accessToken);
                    request.addHeader("Authorization", headerValue);
                }
            }
        };

        // rest adapter for travis API service
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(TRAVIS_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(requestInterceptor)
                .setErrorHandler(new RestErrorHandling())
                .build();
        mApiService = restAdapter.create(TravisApiService.class);

        // rest adapter for github API service
        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(GITHUB_URL)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new RestErrorHandling())
                .build();
        mGithubApiService = restAdapter.create(GithubApiService.class);
    }

    /**
     * Gets travis API service
     *
     * @return Travis API service
     */
    public TravisApiService getApiService() {
        return mApiService;
    }

    /**
     * Gets Github API service
     *
     * @return Github API service
     */
    public GithubApiService getGithubApiService() {
        return mGithubApiService;
    }

}
