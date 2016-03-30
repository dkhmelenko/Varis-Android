package com.khmelenko.lab.travisclient.task;

import com.khmelenko.lab.travisclient.network.request.AuthorizationRequest;
import com.khmelenko.lab.travisclient.task.github.CreateAuthorizationTask;
import com.khmelenko.lab.travisclient.task.github.DeleteAuthorizationTask;
import com.khmelenko.lab.travisclient.task.travis.AuthTask;
import com.khmelenko.lab.travisclient.task.travis.BranchesTask;
import com.khmelenko.lab.travisclient.task.travis.BuildDetailsTask;
import com.khmelenko.lab.travisclient.task.travis.BuildHistoryTask;
import com.khmelenko.lab.travisclient.task.travis.CancelBuildTask;
import com.khmelenko.lab.travisclient.task.travis.FindRepoTask;
import com.khmelenko.lab.travisclient.task.travis.IntentBuildDetailsTask;
import com.khmelenko.lab.travisclient.task.travis.LogTask;
import com.khmelenko.lab.travisclient.task.travis.RequestsTask;
import com.khmelenko.lab.travisclient.task.travis.RestartBuildTask;
import com.khmelenko.lab.travisclient.task.travis.UserReposTask;
import com.khmelenko.lab.travisclient.task.travis.UserTask;

import javax.inject.Inject;

/**
 * Manages the tasks
 *
 * @author Dmytro Khmelenko
 */
public final class TaskManager {

    private final TaskHelper mTaskHelper;

    @Inject
    public TaskManager(TaskHelper taskHelper) {
        mTaskHelper = taskHelper;
    }

    /**
     * Creates new authorization task
     *
     * @param basicAuth Basic GitHub authorization
     * @param request   Request data
     */
    public void createNewAuthorization(String basicAuth, AuthorizationRequest request) {
        CreateAuthorizationTask task = new CreateAuthorizationTask(basicAuth, request);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
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
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Deletes authorization
     *
     * @param basicAuth       Basic GitHub authorization
     * @param authorizationId Authorization ID to delete
     */
    public void deleteAuthorization(String basicAuth, String authorizationId) {
        DeleteAuthorizationTask task = new DeleteAuthorizationTask(basicAuth, authorizationId);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
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
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Starts authentication task
     *
     * @param githubToken Github access token
     */
    public void startAuth(String githubToken) {
        AuthTask task = new AuthTask(githubToken);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Starts searching repository task
     *
     * @param searchText Search text
     */
    public void findRepos(String searchText) {
        FindRepoTask task = new FindRepoTask(searchText);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Gets build history
     *
     * @param repoSlug Repo slug
     */
    public void getBuildHistory(String repoSlug) {
        BuildHistoryTask task = new BuildHistoryTask(repoSlug);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Gets repository branches
     *
     * @param repoSlug Repo slug
     */
    public void getBranches(String repoSlug) {
        BranchesTask task = new BranchesTask(repoSlug);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Gets repository requests
     *
     * @param repoSlug Repo slug
     */
    public void getRequests(String repoSlug) {
        RequestsTask task = new RequestsTask(repoSlug);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Gets user information
     */
    public void getUser() {
        UserTask task = new UserTask();
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Loads user related repositories
     *
     * @param userName User name
     */
    public void userRepos(String userName) {
        UserReposTask task = new UserReposTask(userName);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Loads build details information
     *
     * @param repoSlug Repository slug
     * @param buildId  Build ID
     */
    public void getBuildDetails(String repoSlug, long buildId) {
        BuildDetailsTask task = new BuildDetailsTask(repoSlug, buildId);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Loads an url for the log file
     *
     * @param auth  Authentication
     * @param jobId Job ID
     */
    public void getLogUrl(String auth, long jobId) {
        LogTask task = new LogTask(auth, jobId);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
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
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Cancels build
     *
     * @param buildId Build ID to cancel
     */
    public void cancelBuild(long buildId) {
        CancelBuildTask task = new CancelBuildTask(buildId);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }

    /**
     * Executes the task for intent build details
     *
     * @param url Url
     */
    public void intentBuildDetails(String url) {
        IntentBuildDetailsTask task = new IntentBuildDetailsTask(url);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
    }
}
