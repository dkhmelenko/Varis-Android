package com.khmelenko.lab.varis.mvp;

/**
 * Defines base interface for views
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public interface MvpView {

    /**
     * Shows progress
     */
    void showProgress();

    /**
     * Hides progress
     */
    void hideProgress();
}
