package com.khmelenko.lab.travisclient.task;

import com.khmelenko.lab.travisclient.task.github.AuthTask;

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
        AuthTask task = new AuthTask(clientId, clientSecret, accessCode);
        LoaderAsyncTask.executeTask(task);
    }

}
