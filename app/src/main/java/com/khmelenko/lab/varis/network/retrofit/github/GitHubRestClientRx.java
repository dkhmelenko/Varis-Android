package com.khmelenko.lab.varis.network.retrofit.github;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khmelenko.lab.varis.common.Constants;
import com.khmelenko.lab.varis.network.retrofit.ItemTypeAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * GitHub related REST client
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class GitHubRestClientRx {

    private static final String GITHUB_URL = Constants.GITHUB_URL;

    private GithubApiService mGithubApiService;

    private GitHubRestClientRx() {

        // rest adapter for github API service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_URL)
                .addConverterFactory(GsonConverterFactory.create(constructGsonConverter()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mGithubApiService = retrofit.create(GithubApiService.class);
    }

    /**
     * Creates new instance of the rest client
     *
     * @return Instance
     */
    public static GitHubRestClientRx newInstance() {
        return new GitHubRestClientRx();
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
