package com.khmelenko.lab.varis.presenter;

import android.text.TextUtils;

import com.khmelenko.lab.varis.event.github.CreateAuthorizationSuccessEvent;
import com.khmelenko.lab.varis.event.github.DeleteAuthorizationSuccessEvent;
import com.khmelenko.lab.varis.event.github.GithubAuthorizationFailEvent;
import com.khmelenko.lab.varis.event.travis.AuthFailEvent;
import com.khmelenko.lab.varis.event.travis.AuthSuccessEvent;
import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.network.request.AuthorizationRequest;
import com.khmelenko.lab.varis.network.response.Authorization;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskManager;
import com.khmelenko.lab.varis.util.EncryptionUtils;
import com.khmelenko.lab.varis.util.StringUtils;
import com.khmelenko.lab.varis.view.AuthView;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Authentication presenter
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class AuthPresenter extends MvpPresenter<AuthView> {

    private final TaskManager mTaskManager;
    private final EventBus mEventBus;
    private final TravisRestClient mTravisRestClient;

    private String mBasicAuth;
    private String mSecurityCode;
    private Authorization mAuthorization;

    private boolean mSecurityCodeInput;

    @Inject
    public AuthPresenter(TaskManager taskManager, EventBus eventBus, TravisRestClient travisRestClient) {
        mTaskManager = taskManager;
        mEventBus = eventBus;
        mTravisRestClient = travisRestClient;
    }

    @Override
    public void onAttach() {
        mEventBus.register(this);
        getView().setInputView(mSecurityCodeInput);
    }

    @Override
    public void onDetach() {
        mEventBus.unregister(this);
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
        getView().hideProgress();

        TaskError taskError = event.getTaskError();
        if (taskError.getCode() == TaskError.TWO_FACTOR_AUTH_REQUIRED) {
            mSecurityCodeInput = true;
            getView().showTwoFactorAuth();
        } else {
            getView().showErrorMessage(event.getTaskError().getMessage());
        }
    }

    /**
     * Handles event of success authentication
     *
     * @param event Event data
     */
    public void onEvent(AuthSuccessEvent event) {
        getView().hideProgress();

        // start deletion authorization on Github, because we don't need it anymore
        if (!TextUtils.isEmpty(mSecurityCode)) {
            mTaskManager.deleteAuthorization(mBasicAuth, String.valueOf(mAuthorization.getId()), mSecurityCode);
        } else {
            mTaskManager.deleteAuthorization(mBasicAuth, String.valueOf(mAuthorization.getId()));
        }

        // save access token to settings
        String accessToken = event.getAccessToken();
        AppSettings.putAccessToken(accessToken);

        getView().finishView();
    }

    /**
     * Handles event on failed authentication
     *
     * @param event Event data
     */
    public void onEvent(AuthFailEvent event) {
        getView().hideProgress();
        getView().showErrorMessage(event.getTaskError().getMessage());
    }

    /**
     * Updates server endpoint
     *
     * @param newServer New server endpoint
     */
    public void updateServer(String newServer) {
        AppSettings.putServerUrl(newServer);
        mTravisRestClient.updateTravisEndpoint(newServer);
    }

    /**
     * Executes login action
     *
     * @param userName Username
     * @param password Password
     */
    public void login(String userName, String password) {
        mBasicAuth = EncryptionUtils.generateBasicAuthorization(userName, password);
        mTaskManager.createNewAuthorization(mBasicAuth, prepareAuthorizationRequest());
    }

    /**
     * Executes two-factor auth call
     *
     * @param securityCode Security code
     */
    public void twoFactorAuth(String securityCode) {
        mSecurityCode = securityCode;
        mTaskManager.createNewAuthorization(mBasicAuth, prepareAuthorizationRequest(), securityCode);
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
        return new AuthorizationRequest(scopes, note);
    }
}
