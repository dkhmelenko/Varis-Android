package com.khmelenko.lab.travisclient.view;

import com.khmelenko.lab.travisclient.mvp.MvpView;
import com.khmelenko.lab.travisclient.network.response.Branches;
import com.khmelenko.lab.travisclient.network.response.BuildHistory;
import com.khmelenko.lab.travisclient.network.response.Requests;

/**
 * Repository details view
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public interface RepoDetailsView extends MvpView {

    /**
     * Updates build history
     *
     * @param buildHistory Build history
     */
    void updateBuildHistory(BuildHistory buildHistory);

    /**
     * Updates branches
     *
     * @param branches New branches
     */
    void updateBranches(Branches branches);

    /**
     * Updates pull requests
     *
     * @param requests New pull requests
     */
    void updatePullRequests(Requests requests);

    /**
     * Shows an error on loading Build history
     *
     * @param message Error message
     */
    void showBuildHistoryLoadingError(String message);

    /**
     * Shows an error on loading Branches
     *
     * @param message Error message
     */
    void showBranchesLoadingError(String message);

    /**
     * Shows an error on loading Pull Requests
     *
     * @param message Error message
     */
    void showPullRequestsLoadingError(String message);
}
