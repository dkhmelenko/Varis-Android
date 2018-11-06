package com.khmelenko.lab.varis.network.retrofit.github


import com.khmelenko.lab.varis.common.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit

private const val GITHUB_URL = Constants.GITHUB_URL

/**
 * GitHub related REST client
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class GitHubRestClient(retrofit: Retrofit, okHttpClient: OkHttpClient) {

    /**
     * Gets Github API service
     *
     * @return Github API service
     */
    val apiService: GithubApiService = retrofit.newBuilder()
            .baseUrl(GITHUB_URL)
            .client(okHttpClient)
            .build()
            .create(GithubApiService::class.java)
}
