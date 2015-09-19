package com.khmelenko.lab.travisclient.task;

import com.khmelenko.lab.travisclient.network.request.AuthorizationRequest;
import com.khmelenko.lab.travisclient.task.github.CreateAuthorizationTask;
import com.khmelenko.lab.travisclient.task.github.DeleteAuthorizationTask;
import com.khmelenko.lab.travisclient.task.travis.AuthTask;
import com.khmelenko.lab.travisclient.task.travis.BranchesTask;
import com.khmelenko.lab.travisclient.task.travis.BuildHistoryTask;
import com.khmelenko.lab.travisclient.task.travis.FindRepoTask;
import com.khmelenko.lab.travisclient.task.travis.RequestsTask;

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
     * Deletes authorization
     *
     * @param basicAuth Basic GitHub authorization
     */
    public void deleteAuthorization(String basicAuth) {
        DeleteAuthorizationTask task = new DeleteAuthorizationTask(basicAuth);
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

}
