package com.khmelenko.lab.travisclient.task;

import com.khmelenko.lab.travisclient.task.github.GithubAuthTask;
import com.khmelenko.lab.travisclient.task.travis.AuthTask;
import com.khmelenko.lab.travisclient.task.travis.FindRepoTask;
import com.khmelenko.lab.travisclient.task.travis.RepoStatusTask;

/**
 * Manages the tasks
 *
 * @author Dmytro Khmelenko
 */
public final class TaskManager {

    /**
     * Starts Github authentication task
     *
     * @param clientId     Client ID
     * @param clientSecret Client secret
     * @param accessCode   Access code
     */
    public void startGithubAuth(String clientId, String clientSecret, String accessCode) {
        GithubAuthTask task = new GithubAuthTask(clientId, clientSecret, accessCode);
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

    public void findRepos(String searchText) {
        FindRepoTask task = new FindRepoTask(searchText);
        LoaderAsyncTask.executeTask(task);
    }

    /**
     * Gets repository status
     *
     * @param repoSlug Repo slug
     */
    public void getRepoStatus(String repoSlug) {
        RepoStatusTask task = new RepoStatusTask(repoSlug);
        LoaderAsyncTask.executeTask(task);
    }

}
