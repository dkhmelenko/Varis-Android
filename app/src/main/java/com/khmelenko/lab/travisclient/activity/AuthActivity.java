package com.khmelenko.lab.travisclient.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
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

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Authentication activity
 *
 * @author Dmytro Khmelenko
 */
public class AuthActivity extends AppCompatActivity implements AuthFragment.OnLoginActionListener,
        SecurityCodeFragment.OnSecurityCodeAction {

    private static final String SECURITY_CODE_INPUT = "securityCodeInput";

    private AuthFragment mAuthFragment;
    private SecurityCodeFragment mSecurityCodeFragment;
    private ProgressDialog mProgressDialog;

    private EventBus mEventBus = EventBus.getDefault();
    private TaskManager mTaskManager = new TaskManager();

    private String mBasicAuth;
    private String mSecurityCode;
    private Authorization mAuthorization;

    private boolean mSecurityCodeInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        initToolbar();

        if (savedInstanceState != null) {
            mSecurityCodeInput = savedInstanceState.getBoolean(SECURITY_CODE_INPUT);
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
        mAuthFragment = AuthFragment.newInstance();
        addFragment(R.id.auth_container, mAuthFragment, "AuthFragment");
    }

    /**
     * Shows the input for security code
     */
    private void showSecurityCodeInput() {
        if (mAuthFragment != null) {
            detachFragment(mAuthFragment);
        }

        mSecurityCodeFragment = SecurityCodeFragment.newInstance();
        addFragment(R.id.auth_container, mSecurityCodeFragment, "SecurityCodeFragment");
    }

    /**
     * Adds new fragment
     *
     * @param containerViewId ID of the container view for fragment
     * @param fragment        Fragment instance
     * @param fragmentTag     Fragment tag
     */
    protected void addFragment(@IdRes int containerViewId,
                               @NonNull Fragment fragment,
                               @NonNull String fragmentTag) {
        if (!fragment.isAdded()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(containerViewId, fragment, fragmentTag)
                    .disallowAddToBackStack()
                    .commit();
        }
    }

    /**
     * Replaces fragment
     *
     * @param containerViewId    ID of the container view for fragment
     * @param fragment           Fragment instance
     * @param fragmentTag        Fragment tag
     * @param backStackStateName Name in back stack
     */
    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag,
                                   @Nullable String backStackStateName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
    }

    /**
     * Detaches fragment
     *
     * @param fragment Fragment
     */
    protected void detachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .commit();
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
