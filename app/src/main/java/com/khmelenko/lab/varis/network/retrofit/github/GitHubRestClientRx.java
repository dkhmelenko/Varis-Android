package com.khmelenko.lab.varis.network.retrofit.github;


import com.khmelenko.lab.varis.common.Constants;

import retrofit2.Retrofit;


/**
 * GitHub related REST client
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class GitHubRestClientRx {

    private static final String GITHUB_URL = Constants.GITHUB_URL;

    private final Retrofit mRetrofit;

    private GithubApiServiceRx mGithubApiService;

    public GitHubRestClientRx(Retrofit retrofit) {
        mRetrofit = retrofit;

        // rest adapter for github API service
        Retrofit newRetrofit = mRetrofit.newBuilder()
                .baseUrl(GITHUB_URL)
                .build();
        mGithubApiService = newRetrofit.create(GithubApiServiceRx.class);
    }

    /**
     * Gets Github API service
     *
     * @return Github API service
     */
    public GithubApiServiceRx getApiService() {
        return mGithubApiService;
    }

}
