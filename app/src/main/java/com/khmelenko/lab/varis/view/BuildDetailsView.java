package com.khmelenko.lab.varis.view;

import com.khmelenko.lab.varis.log.LogEntryComposite;
import com.khmelenko.lab.varis.mvp.MvpView;
import com.khmelenko.lab.varis.network.response.BuildDetails;
import com.khmelenko.lab.varis.network.response.Job;

import java.util.List;

/**
 * View for BuildDetails
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public interface BuildDetailsView extends MvpView {

    /**
     * Shows loading error
     *
     * @param message Error message
     */
    void showLoadingError(String message);

    /**
     * Updates build details
     *
     * @param buildDetails Build details
     */
    void updateBuildDetails(BuildDetails buildDetails);

    /**
     * Shows an error on loading Log
     */
    void showLogError();

    /**
     * Sets content of the log
     *
     * @param log Parsed LogEntryComposite
     */
    void setLog(LogEntryComposite log);

    /**
     * Shows the list of build jobs
     *
     * @param jobs Build jobs
     */
    void showBuildJobs(List<Job> jobs);

    /**
     * Shows build logs
     */
    void showBuildLogs();

    /**
     * Shows additional available actions for build
     *
     * @param details Build details
     */
    void showAdditionalActionsForBuild(BuildDetails details);
}
