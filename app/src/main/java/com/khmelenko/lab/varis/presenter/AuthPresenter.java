package com.khmelenko.lab.varis.presenter;

import android.text.TextUtils;

import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.network.request.AccessTokenRequest;
import com.khmelenko.lab.varis.network.request.AuthorizationRequest;
import com.khmelenko.lab.varis.network.response.AccessToken;
import com.khmelenko.lab.varis.network.response.Authorization;
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.github.GithubApiService;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.util.EncryptionUtils;
import com.khmelenko.lab.varis.util.StringUtils;
import com.khmelenko.lab.varis.view.AuthView;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Authentication presenter
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class AuthPresenter extends MvpPresenter<AuthView> {

    private final TravisRestClient mTravisRestClient;
    private final GitHubRestClient mGitHubRestClient;
    private final AppSettings mAppSettings;

    private final CompositeDisposable mSubscriptions;

    private String mBasicAuth;
    private String mSecurityCode;
    private Authorization mAuthorization;

    private boolean mSecurityCodeInput;

    @Inject
    public AuthPresenter(TravisRestClient travisRestClient, GitHubRestClient gitHubRestClient, AppSettings appSettings) {
        mTravisRestClient = travisRestClient;
        mGitHubRestClient = gitHubRestClient;
        mAppSettings = appSettings;

        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void onAttach() {
        getView().setInputView(mSecurityCodeInput);
    }

    @Override
    public void onDetach() {
        mSubscriptions.clear();
    }

    /**
     * Updates server endpoint
     *
     * @param newServer New server endpoint
     */
    public void updateServer(String newServer) {
        mAppSettings.putServerUrl(newServer);
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

        doLogin(getAuthorizationJob(false));
    }

    /**
     * Executes two-factor auth call
     *
     * @param securityCode Security code
     */
    public void twoFactorAuth(String securityCode) {
        mSecurityCode = securityCode;
        doLogin(getAuthorizationJob(true));
    }

    /**
     * Returns server URL
     *
     * @return Server URL
     */
    public String getServerUrl() {
        return mAppSettings.getServerUrl();
    }

    private Single<Authorization> getAuthorizationJob(boolean twoFactorAuth) {
        if (twoFactorAuth) {
            return mGitHubRestClient.getApiService()
                    .createNewAuthorization(mBasicAuth, mSecurityCode, prepareAuthorizationRequest());
        } else {
            return mGitHubRestClient.getApiService()
                    .createNewAuthorization(mBasicAuth, prepareAuthorizationRequest());
        }
    }

    private void doLogin(Single<Authorization> authorizationJob) {
        Disposable subscription = authorizationJob
                .flatMap(this::doAuthorization)
                .doOnSuccess(this::saveAccessToken)
                .doAfterSuccess(accessToken -> cleanUpAfterAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((authorization, throwable) -> {
                    getView().hideProgress();

                    if (throwable == null) {
                        getView().finishView();
                    } else {
                        HttpException httpException = (HttpException) throwable;
                        if (isTwoFactorAuthRequired(httpException)) {
                            mSecurityCodeInput = true;
                            getView().showTwoFactorAuth();
                        } else {
                            getView().showErrorMessage(throwable.getMessage());
                        }
                    }
                });

        mSubscriptions.add(subscription);
    }

    private void cleanUpAfterAuthorization() {
        // start deletion authorization on Github, because we don't need it anymore
        Single<Object> cleanUpJob;
        if (!TextUtils.isEmpty(mSecurityCode)) {
            cleanUpJob = mGitHubRestClient.getApiService()
                    .deleteAuthorization(mBasicAuth, mSecurityCode, String.valueOf(mAuthorization.getId()));
        } else {
            cleanUpJob = mGitHubRestClient.getApiService()
                    .deleteAuthorization(mBasicAuth, String.valueOf(mAuthorization.getId()));
        }
        Disposable task = cleanUpJob
                .onErrorReturn(throwable -> new Object())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        mSubscriptions.add(task);
    }

    private void saveAccessToken(AccessToken accessToken) {
        // save access token to settings
        String token = accessToken.getAccessToken();
        mAppSettings.putAccessToken(token);
    }

    private Single<AccessToken> doAuthorization(Authorization authorization) {
        mAuthorization = authorization;
        AccessTokenRequest request = new AccessTokenRequest();
        request.setGithubToken(authorization.getToken());
        return mTravisRestClient.getApiService().auth(request);
    }

    private boolean isTwoFactorAuthRequired(HttpException exception) {
        Response response = exception.response();

        boolean twoFactorAuthRequired = false;
        for (String header : response.headers().names()) {
            if (GithubApiService.TWO_FACTOR_HEADER.equals(header)) {
                twoFactorAuthRequired = true;
                break;
            }
        }

        return response.code() == HttpURLConnection.HTTP_UNAUTHORIZED && twoFactorAuthRequired;
    }

    /**
     * Prepares authorization request
     *
     * @return Authorization request
     */
    private AuthorizationRequest prepareAuthorizationRequest() {
        List<String> scopes = Arrays.asList("read:org", "user:email", "repo_deployment",
                "repo:status", "write:repo_hook", "repo");
        String note = String.format("varis_client_%1$s", StringUtils.getRandomString());
        return new AuthorizationRequest(scopes, note);
    }
}
