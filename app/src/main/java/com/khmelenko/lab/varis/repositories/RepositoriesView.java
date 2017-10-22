package com.khmelenko.lab.varis.repositories;

import android.support.annotation.Nullable;

import com.khmelenko.lab.varis.mvp.MvpView;
import com.khmelenko.lab.varis.network.response.Repo;
import com.khmelenko.lab.varis.network.response.User;

import java.util.List;

/**
 * Repositories view
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public interface RepositoriesView extends MvpView {

    /**
     * Updates user data
     *
     * @param user User data
     */
    void updateUserData(User user);

    /**
     * Sets the list of repositories
     *
     * @param repos Repositories
     */
    void setRepos(List<Repo> repos);

    /**
     * Updates menu state
     *
     * @param accessToken Access token
     */
    void updateMenuState(@Nullable String accessToken);

    /**
     * Shows an error message
     *
     * @param message Error message
     */
    void showError(String message);
}
