package com.khmelenko.lab.varis.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.TravisApp;
import com.khmelenko.lab.varis.fragment.AuthFragment;
import com.khmelenko.lab.varis.fragment.SecurityCodeFragment;
import com.khmelenko.lab.varis.mvp.MvpActivity;
import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.presenter.AuthPresenter;
import com.khmelenko.lab.varis.util.PresenterKeeper;
import com.khmelenko.lab.varis.view.AuthView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

/**
 * Authentication activity
 *
 * @author Dmytro Khmelenko
 */
public final class AuthActivity extends MvpActivity<AuthPresenter> implements
                                                                   AuthView,
                                                                   AuthFragment.OnLoginActionListener,
                                                                   SecurityCodeFragment.OnSecurityCodeAction {

    private static final String SECURITY_CODE_INPUT = "securityCodeInput";
    private static final String AUTH_FRAGMENT_TAG = "AuthFragment";
    private static final String SECURITY_CODE_FRAGMENT_TAG = "SecurityCodeFragment";

    private ProgressDialog mProgressDialog;

    @Inject
    AuthPresenter mPresenter;

    @Inject
    PresenterKeeper<MvpPresenter> mPresenterKeeper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        initToolbar();
    }

    /**
     * Shows login section
     */
    private void showLoginSection() {
        AuthFragment authFragment = (AuthFragment) getSupportFragmentManager().findFragmentByTag(AUTH_FRAGMENT_TAG);
        if (authFragment == null) {
            authFragment = AuthFragment.newInstance(mPresenter.getServerUrl());
            addFragment(R.id.auth_container, authFragment, AUTH_FRAGMENT_TAG);
        }
    }

    /**
     * Shows the input for security code
     */
    private void showSecurityCodeInput() {
        SecurityCodeFragment securityCodeFragment = (SecurityCodeFragment) getSupportFragmentManager().findFragmentByTag(SECURITY_CODE_FRAGMENT_TAG);
        if (securityCodeFragment == null) {
            securityCodeFragment = SecurityCodeFragment.newInstance();
            replaceFragment(R.id.auth_container, securityCodeFragment, SECURITY_CODE_FRAGMENT_TAG);
        }
    }

    @Override
    protected AuthPresenter getPresenter() {
        MvpPresenter presenter = mPresenterKeeper.get(AuthPresenter.class);
        if (presenter != null) {
            mPresenter = (AuthPresenter) presenter;
        }
        return mPresenter;
    }

    @Override
    protected void attachPresenter() {
        getPresenter().attach(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenterKeeper.put(AuthPresenter.class, mPresenter);
    }

    /**
     * Initializes toolbar
     */
    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public void onLogin(String userName, String password) {
        showProgress();
        getPresenter().login(userName, password);
    }

    @Override
    public void onChangeServer(String newServer) {
        getPresenter().updateServer(newServer);
    }

    @Override
    public void onSecurityCodeInput(String securityCode) {
        showProgress();
        getPresenter().twoFactorAuth(securityCode);
    }

    @Override
    public void showProgress() {
        mProgressDialog = ProgressDialog.show(this, "", getString(R.string.loading_msg));
    }

    @Override
    public void hideProgress() {
        mProgressDialog.dismiss();
    }

    @Override
    public void finishView() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void showErrorMessage(String message) {
        String msg = getString(R.string.error_failed_auth, message);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTwoFactorAuth() {
        showSecurityCodeInput();
    }

    @Override
    public void setInputView(boolean securityCodeInput) {
        if (securityCodeInput) {
            showSecurityCodeInput();
        } else {
            showLoginSection();
        }
    }
}
