package com.khmelenko.lab.travisclient.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.TravisApp;
import com.khmelenko.lab.travisclient.event.github.CreateAuthorizationSuccessEvent;
import com.khmelenko.lab.travisclient.event.github.DeleteAuthorizationSuccessEvent;
import com.khmelenko.lab.travisclient.event.github.GithubAuthorizationFailEvent;
import com.khmelenko.lab.travisclient.event.travis.AuthFailEvent;
import com.khmelenko.lab.travisclient.event.travis.AuthSuccessEvent;
import com.khmelenko.lab.travisclient.fragment.AuthFragment;
import com.khmelenko.lab.travisclient.fragment.SecurityCodeFragment;
import com.khmelenko.lab.travisclient.network.request.AuthorizationRequest;
import com.khmelenko.lab.travisclient.network.response.Authorization;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.util.EncryptionUtils;
import com.khmelenko.lab.travisclient.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Authentication activity
 *
 * @author Dmytro Khmelenko
 */
public final class AuthActivity extends BaseActivity implements AuthFragment.OnLoginActionListener,
        SecurityCodeFragment.OnSecurityCodeAction {

    private static final String SECURITY_CODE_INPUT = "securityCodeInput";
    private static final String AUTHORIZATION_HEADER = "AuthorizationHeader";

    private static final String AUTH_FRAGMENT_TAG = "AuthFragment";
    private static final String SECURITY_CODE_FRAGMENT_TAG = "SecurityCodeFragment";

    private AuthFragment mAuthFragment;
    private SecurityCodeFragment mSecurityCodeFragment;
    private ProgressDialog mProgressDialog;

    private TaskManager mTaskManager = new TaskManager();

    @Inject
    EventBus mEventBus;

    private String mBasicAuth;
    private String mSecurityCode;
    private Authorization mAuthorization;

    private boolean mSecurityCodeInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        TravisApp.instance().getNetworkComponent().inject(this);

        initToolbar();

        if (savedInstanceState != null) {
            mSecurityCodeInput = savedInstanceState.getBoolean(SECURITY_CODE_INPUT);
            mBasicAuth = savedInstanceState.getString(AUTHORIZATION_HEADER);
        }

        if (mSecurityCodeInput) {
            showSecurityCodeInput();
        } else {
            showLoginSection();
        }
    }

    /**
     * Shows login section
     */
    private void showLoginSection() {
        mAuthFragment = (AuthFragment) getSupportFragmentManager().findFragmentByTag(AUTH_FRAGMENT_TAG);
        if (mAuthFragment == null) {
            mAuthFragment = AuthFragment.newInstance();
            addFragment(R.id.auth_container, mAuthFragment, AUTH_FRAGMENT_TAG);
        }
    }

    /**
     * Shows the input for security code
     */
    private void showSecurityCodeInput() {

        mSecurityCodeFragment = (SecurityCodeFragment) getSupportFragmentManager().findFragmentByTag(SECURITY_CODE_FRAGMENT_TAG);
        if (mSecurityCodeFragment == null) {
            mSecurityCodeFragment = SecurityCodeFragment.newInstance();
            replaceFragment(R.id.auth_container, mSecurityCodeFragment, SECURITY_CODE_FRAGMENT_TAG);
        }
    }

    /**
     * Prepares authorization request
     *
     * @return Authorization request
     */
    private AuthorizationRequest prepareAuthorizationRequest() {
        List<String> scopes = Arrays.asList("read:org", "user:email", "repo_deployment",
                "repo:status", "write:repo_hook", "repo");
        String note = String.format("travis_client_%1$s", StringUtils.getRandomString());
        AuthorizationRequest request = new AuthorizationRequest(scopes, note);
        return request;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SECURITY_CODE_INPUT, mSecurityCodeInput);
        outState.putString(AUTHORIZATION_HEADER, mBasicAuth);
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

    /**
     * Handles success creation new authorization
     *
     * @param event Event data
     */
    public void onEvent(CreateAuthorizationSuccessEvent event) {
        mAuthorization = event.getAuthorization();
        mTaskManager.startAuth(mAuthorization.getToken());
    }

    /**
     * Handles success deletion authorization
     *
     * @param event Event data
     */
    public void onEvent(DeleteAuthorizationSuccessEvent event) {
        // ignoring the result of deletion
    }

    /**
     * Handles event of github failed authentication
     *
     * @param event Event data
     */
    public void onEvent(GithubAuthorizationFailEvent event) {
        mProgressDialog.dismiss();

        TaskError taskError = event.getTaskError();
        if (taskError.getCode() == TaskError.TWO_FACTOR_AUTH_REQUIRED) {
            mSecurityCodeInput = true;
            showSecurityCodeInput();
        } else {
            String msg = getString(R.string.error_failed_auth, event.getTaskError().getMessage());
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles event of success authentication
     *
     * @param event Event data
     */
    public void onEvent(AuthSuccessEvent event) {
        mProgressDialog.dismiss();

        // start deletion authorization on Github, because we don't need it anymore
        if (!TextUtils.isEmpty(mSecurityCode)) {
            mTaskManager.deleteAuthorization(mBasicAuth, String.valueOf(mAuthorization.getId()), mSecurityCode);
        } else {
            mTaskManager.deleteAuthorization(mBasicAuth, String.valueOf(mAuthorization.getId()));
        }

        // save access token to settings
        String accessToken = event.getAccessToken();
        AppSettings.putAccessToken(accessToken);

        setResult(RESULT_OK);
        finish();
    }

    /**
     * Handles event on failed authentication
     *
     * @param event Event data
     */
    public void onEvent(AuthFailEvent event) {
        mProgressDialog.dismiss();

        String msg = getString(R.string.error_failed_auth, event.getTaskError().getMessage());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLogin(String userName, String password) {
        mProgressDialog = ProgressDialog.show(this, "", getString(R.string.loading_msg));

        mBasicAuth = EncryptionUtils.generateBasicAuthorization(userName, password);
        mTaskManager.createNewAuthorization(mBasicAuth, prepareAuthorizationRequest());
    }

    @Override
    public void onSecurityCodeInput(String securityCode) {
        mSecurityCode = securityCode;
        mProgressDialog = ProgressDialog.show(this, "", getString(R.string.loading_msg));
        mTaskManager.createNewAuthorization(mBasicAuth, prepareAuthorizationRequest(), securityCode);
    }
}
