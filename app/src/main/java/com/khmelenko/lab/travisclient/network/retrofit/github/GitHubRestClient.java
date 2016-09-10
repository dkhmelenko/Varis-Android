package com.khmelenko.lab.travisclient.network.retrofit.github;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.network.retrofit.ItemTypeAdapterFactory;
import com.khmelenko.lab.travisclient.network.retrofit.RestErrorHandling;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * GitHub related REST client
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class GitHubRestClient {

    private static final String GITHUB_URL = Constants.GITHUB_URL;

    private GithubApiService mGithubApiService;

    private GitHubRestClient() {

        // rest adapter for github API service
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(GITHUB_URL)
                .setConverter(new GsonConverter(constructGsonConverter()))
                .setErrorHandler(new RestErrorHandling())
                .build();
        mGithubApiService = restAdapter.create(GithubApiService.class);
    }

    /**
     * Creates new instance of the rest client
     *
     * @return Instance
     */
    public static GitHubRestClient newInstance() {
        return new GitHubRestClient();
    }

    /**
     * Construct Gson converter
     *
     * @return Gson converter
     */
    private Gson constructGsonConverter() {
        return new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();
    }

    /**
     * Gets Github API service
     *
     * @return Github API service
     */
    public GithubApiService getApiService() {
        return mGithubApiService;
    }

}
