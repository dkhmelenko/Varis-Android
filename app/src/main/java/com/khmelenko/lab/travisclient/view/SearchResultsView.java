package com.khmelenko.lab.travisclient.view;

import com.khmelenko.lab.travisclient.mvp.MvpView;
import com.khmelenko.lab.travisclient.network.response.Repo;

import java.util.List;

/**
 * Search results view
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public interface SearchResultsView extends MvpView {

    /**
     * Set the results with the list of repositories
     *
     * @param repos Found repositories
     */
    void setSearchResults(List<Repo> repos);

    /**
     * Shows loading error
     *
     * @param message Error message
     */
    void showLoadingError(String message);
}
