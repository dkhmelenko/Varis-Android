package com.khmelenko.lab.varis.network.retrofit.travis

import com.khmelenko.lab.varis.network.request.AccessTokenRequest
import com.khmelenko.lab.varis.network.response.AccessToken
import com.khmelenko.lab.varis.network.response.Branch
import com.khmelenko.lab.varis.network.response.Branches
import com.khmelenko.lab.varis.network.response.BuildDetails
import com.khmelenko.lab.varis.network.response.BuildHistory
import com.khmelenko.lab.varis.network.response.Logs
import com.khmelenko.lab.varis.network.response.Repo
import com.khmelenko.lab.varis.network.response.Request
import com.khmelenko.lab.varis.network.response.Requests
import com.khmelenko.lab.varis.network.response.User

import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface TravisApiService {

    // repositories
    @get:GET("/repos")
    val repos: Single<List<Repo>>


    // users
    @get:GET("/users")
    val user: Single<User>

    @POST("/auth/github")
    fun auth(@Body accessToken: AccessTokenRequest): Single<AccessToken>

    @GET("/repos")
    fun getRepos(@Query("search") search: String): Single<List<Repo>>

    @GET("/repos/{repositoryId}")
    fun getRepo(@Path("repositoryId") repositoryId: Long): Single<Repo>

    @GET("/repos/{repositorySlug}")
    fun getRepo(@Path(value = "repositorySlug", encoded = true) repositorySlug: String): Single<Repo>

    @GET("/repos")
    fun getUserRepos(@Query("member") userName: String): Single<List<Repo>>


    // branches
    @GET("/repos/{repositoryId}/branches")
    fun getBranches(@Path("repositoryId") repositoryId: Long): Single<Branches>

    @GET("/repos/{repositorySlug}/branches")
    fun getBranches(@Path(value = "repositorySlug", encoded = true) repositorySlug: String): Single<Branches>

    @GET("/repos/{repositoryId}/branches/{branch}")
    fun getBranch(@Path("repositoryId") repositoryId: Long, @Path("branch") branch: String): Single<Branch>

    @GET("/repos/{repositorySlug}/branches/{branch}")
    fun getBranch(@Path(value = "repositorySlug", encoded = true) repositorySlug: String, @Path("branch") branch: String): Single<Branch>


    // builds
    @GET("/repos/{repositoryId}/builds")
    fun getBuilds(@Path("repositoryId") repositoryId: Long): Single<BuildHistory>

    @GET("/repos/{repositorySlug}/builds")
    fun getBuilds(@Path(value = "repositorySlug", encoded = true) repositorySlug: String): Single<BuildHistory>

    @GET("/repos/{repositoryId}/builds?event_type=pull_request")
    fun getPullRequestBuilds(@Path("repositoryId") repositoryId: Long): Single<BuildHistory>

    @GET("/repos/{repositorySlug}/builds?event_type=pull_request")
    fun getPullRequestBuilds(@Path(value = "repositorySlug", encoded = true) repositorySlug: String): Single<BuildHistory>

    @GET("/repos/{repositoryId}/builds/{buildId}")
    fun getBuild(@Path("repositoryId") repositoryId: Long, @Path("buildId") buildId: Long): Single<BuildDetails>

    @GET("/repos/{repositorySlug}/builds/{buildId}")
    fun getBuild(@Path(value = "repositorySlug", encoded = true) repositorySlug: String, @Path("buildId") buildId: Long): Single<BuildDetails>

    @POST("/builds/{buildId}/cancel")
    fun cancelBuild(@Path("buildId") buildId: Long, @Body emptyBody: RequestBody): Single<Any>

    @POST("/builds/{buildId}/restart")
    fun restartBuild(@Path("buildId") buildId: Long, @Body emptyBody: RequestBody): Single<Any>


    // requests
    @GET("/requests/{requestId}")
    fun getRequest(@Path("requestId") requestId: Long): Single<Request>

    @GET("/requests")
    fun getRequests(@Query("repository_id") repositoryId: Long): Single<Requests>

    @GET("/requests")
    fun getRequests(@Query("slug") repositorySlug: String): Single<Requests>


    // logs
    @GET("/jobs/{jobId}/log")
    fun getLogRaw(@Path("jobId") jobId: Long): Single<String>

    @GET("/logs/{logId}")
    fun getLogs(@Path("logId") logId: Long): Single<Logs>
}
