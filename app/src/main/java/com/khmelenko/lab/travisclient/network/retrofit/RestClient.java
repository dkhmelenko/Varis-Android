package com.khmelenko.lab.travisclient.network.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khmelenko.lab.travisclient.common.Constants;

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
                request.addHeader("User-Agent", "TravisClient/1.0.0");
                request.addHeader("Accept", "application/vnd.travis-ci.2+json");
            }
        };

        // rest adapter for travis API service
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(TRAVIS_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(requestInterceptor)
                .build();
        mApiService = restAdapter.create(TravisApiService.class);

        // rest adapter for github API service
        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(GITHUB_URL)
                .setConverter(new GsonConverter(gson))
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
