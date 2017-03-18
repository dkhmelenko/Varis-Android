package com.khmelenko.lab.varis.view;

import com.khmelenko.lab.varis.mvp.MvpView;

/**
 * Authentication view
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public interface AuthView extends MvpView {

    /**
     * Finishes the view
     */
    void finishView();

    /**
     * Shows error message
     *
     * @param message Error message
     */
    void showErrorMessage(String message);

    /**
     * Shows two-factor authentication
     */
    void showTwoFactorAuth();

    /**
     * Set appropriate view for showing input
     *
     * @param securityCodeInput True, if security code exists
     */
    void setInputView(boolean securityCodeInput);
}
