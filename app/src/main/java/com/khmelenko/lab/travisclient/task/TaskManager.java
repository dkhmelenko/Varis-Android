package com.khmelenko.lab.travisclient.task;

import com.khmelenko.lab.travisclient.network.request.AuthorizationRequest;
import com.khmelenko.lab.travisclient.task.github.CreateAuthorizationTask;
import com.khmelenko.lab.travisclient.task.github.DeleteAuthorizationTask;
import com.khmelenko.lab.travisclient.task.travis.AuthTask;
import com.khmelenko.lab.travisclient.task.travis.BranchesTask;
import com.khmelenko.lab.travisclient.task.travis.BuildDetailsTask;
import com.khmelenko.lab.travisclient.task.travis.BuildHistoryTask;
import com.khmelenko.lab.travisclient.task.travis.FindRepoTask;
import com.khmelenko.lab.travisclient.task.travis.LogTask;
import com.khmelenko.lab.travisclient.task.travis.RequestsTask;
import com.khmelenko.lab.travisclient.task.travis.RestartBuildTask;
import com.khmelenko.lab.travisclient.task.travis.UserReposTask;
import com.khmelenko.lab.travisclient.task.travis.UserTask;

/**
 * Manages the tasks
 *
 * @author Dmytro Khmelenko
 */
public final class TaskManager {

    /**
     * Creates new authorization task
     *
     * @param basicAuth Basic GitHub authorization
     * @param request   Request data
     */
    public void createNewAuthorization(String basicAuth, AuthorizationRequest request) {
        CreateAuthorizationTask task = new CreateAuthorizationTask(basicAuth, request);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Creates new authorization task
     *
     * @param basicAuth     Basic GitHub authorization
     * @param request       Request data
     * @param twoFactorCode Two factor authorization code
     */
    public void createNewAuthorization(String basicAuth, AuthorizationRequest request, String twoFactorCode) {
        CreateAuthorizationTask task = new CreateAuthorizationTask(basicAuth, request, twoFactorCode);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Deletes authorization
     *
     * @param basicAuth       Basic GitHub authorization
     * @param authorizationId Authorization ID to delete
     */
    public void deleteAuthorization(String basicAuth, String authorizationId) {
        DeleteAuthorizationTask task = new DeleteAuthorizationTask(basicAuth, authorizationId);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Deletes authorization
     *
     * @param basicAuth       Basic GitHub authorization
     * @param authorizationId Authorization ID to delete
     * @param twoFactorCode   Two factor authorization code
     */
    public void deleteAuthorization(String basicAuth, String authorizationId, String twoFactorCode) {
        DeleteAuthorizationTask task = new DeleteAuthorizationTask(basicAuth, authorizationId, twoFactorCode);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Starts authentication task
     *
     * @param githubToken Github access token
     */
    public void startAuth(String githubToken) {
        AuthTask task = new AuthTask(githubToken);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Starts searching repository task
     *
     * @param searchText Search text
     */
    public void findRepos(String searchText) {
        FindRepoTask task = new FindRepoTask(searchText);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Gets build history
     *
     * @param repoSlug Repo slug
     */
    public void getBuildHistory(String repoSlug) {
        BuildHistoryTask task = new BuildHistoryTask(repoSlug);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Gets repository branches
     *
     * @param repoSlug Repo slug
     */
    public void getBranches(String repoSlug) {
        BranchesTask task = new BranchesTask(repoSlug);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Gets repository requests
     *
     * @param repoSlug Repo slug
     */
    public void getRequests(String repoSlug) {
        RequestsTask task = new RequestsTask(repoSlug);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Gets user information
     */
    public void getUser() {
        UserTask task = new UserTask();
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Loads user related repositories
     *
     * @param userName User name
     */
    public void userRepos(String userName) {
        UserReposTask task = new UserReposTask(userName);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Loads build details information
     *
     * @param repoSlug Repository slug
     * @param buildId  Build ID
     */
    public void getBuildDetails(String repoSlug, long buildId) {
        BuildDetailsTask task = new BuildDetailsTask(repoSlug, buildId);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Loads an url for the log file
     *
     * @param auth  Authentication
     * @param jobId Job ID
     */
    public void getLogUrl(String auth, long jobId) {
        LogTask task = new LogTask(auth, jobId);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Loads an url for the log file
     *
     * @param jobId Job ID
     */
    public void getLogUrl(long jobId) {
        getLogUrl(null, jobId);
    }

    /**
     * Restarts build
     *
     * @param buildId Build ID to restart
     */
    public void restartBuild(long buildId) {
        RestartBuildTask task = new RestartBuildTask(buildId);
        LoaderAsyncTask.executeTask(task);
    }
}
