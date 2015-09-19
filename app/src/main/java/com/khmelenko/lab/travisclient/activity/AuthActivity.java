package com.khmelenko.lab.travisclient.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.event.github.CreateAuthorizationSuccessEvent;
import com.khmelenko.lab.travisclient.event.github.DeleteAuthorizationSuccessEvent;
import com.khmelenko.lab.travisclient.event.github.GithubAuthorizationFailEvent;
import com.khmelenko.lab.travisclient.event.travis.AuthFailEvent;
import com.khmelenko.lab.travisclient.event.travis.AuthSuccessEvent;
import com.khmelenko.lab.travisclient.network.request.AuthorizationRequest;
import com.khmelenko.lab.travisclient.network.response.Authorization;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.util.EncryptionUtils;
import com.khmelenko.lab.travisclient.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Authentication activity
 *
 * @author Dmytro Khmelenko
 */
public class AuthActivity extends AppCompatActivity {

    @Bind(R.id.auth_username)
    EditText mUsername;

    @Bind(R.id.auth_password)
    EditText mPassword;

    @Bind(R.id.auth_login_btn)
    Button mLoginBtn;

    private ProgressDialog mProgressDialog;

    private EventBus mEventBus = EventBus.getDefault();
    private TaskManager mTaskManager = new TaskManager();

    private String mBasicAuth;
    private Authorization mAuthorization;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        initToolbar();

    }

    @OnClick(R.id.auth_login_btn)
    public void startAuthorization() {
        if (areCredentialsValid()) {
            mProgressDialog = ProgressDialog.show(this, "", getString(R.string.loading_msg));
            mBasicAuth = EncryptionUtils.generateBasicAuthorization(
                    mUsername.getText().toString(), mPassword.getText().toString());
            mTaskManager.createNewAuthorization(mBasicAuth, prepareAuthorizationRequest());
        }
    }

    /**
     * Prepares authorization request
     *
     * @return Authorization request
     */
    private AuthorizationRequest prepareAuthorizationRequest() {
        List<String> scopes = Arrays.asList("read:org", "user:email", "repo_deployment",
                "repo:status", "write:repo_hook");
        String note = String.format("travis_client_%1$s", StringUtils.getRandomString());
        AuthorizationRequest request = new AuthorizationRequest(scopes, note);
        return request;
    }

    /**
     * Does validation of the inputted credentials
     *
     * @return True, if inputted credentials are valid. False otherwise
     */
    private boolean areCredentialsValid() {
        boolean valid = true;
        if (TextUtils.isEmpty(mUsername.getText())) {
            valid = false;
            mUsername.setError(getString(R.string.auth_invalid_username_msg));
        }

        if (TextUtils.isEmpty(mPassword.getText())) {
            valid = false;
            mPassword.setError(getString(R.string.auth_invalid_password_msg));
        }
        return valid;
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
        mProgressDialog.dismiss();

    }

    /**
     * Handles event of github failed authentication
     *
     * @param event Event data
     */
    public void onEvent(GithubAuthorizationFailEvent event) {
        mProgressDialog.dismiss();

        // TODO Improve error handling
        Toast.makeText(this, event.getTaskError().getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles event of success authentication
     *
     * @param event Event data
     */
    public void onEvent(AuthSuccessEvent event) {
        mProgressDialog.dismiss();
        mTaskManager.deleteAuthorization(mBasicAuth, String.valueOf(mAuthorization.getId()));

        String accessToken = event.getAccessToken();

        // TODO Save access token

        finish();
    }

    /**
     * Handles event on failed authentication
     *
     * @param event Event data
     */
    public void onEvent(AuthFailEvent event) {
        mProgressDialog.dismiss();

        // TODO Improve error handling
        Toast.makeText(this, event.getTaskError().getMessage(), Toast.LENGTH_SHORT).show();
    }


}
