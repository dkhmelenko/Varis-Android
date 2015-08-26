package com.khmelenko.lab.travisclient.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.event.github.GithubAuthFailEvent;
import com.khmelenko.lab.travisclient.event.github.GithubAuthSuccessEvent;
import com.khmelenko.lab.travisclient.event.travis.AuthFailEvent;
import com.khmelenko.lab.travisclient.event.travis.AuthSuccessEvent;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.util.AssetsUtils;

import java.util.Properties;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Authentication activity
 *
 * @author Dmytro Khmelenko
 */
public class AuthActivity extends AppCompatActivity {

    private static final String OAUTH_URL = "https://github.com/login/oauth/authorize";

    private String mClientId;
    private String mClientSecret;

    @Bind(R.id.webView)
    WebView mWebView;

    private EventBus mEventBus = EventBus.getDefault();
    private TaskManager mTaskManager = new TaskManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        initToolbar();

        // load required properties
        Properties properties = AssetsUtils.getProperties(Constants.KEY_PROPERTIES, this);
        mClientId = properties.getProperty("clientId");
        mClientSecret = properties.getProperty("clientSecret");

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
                                      public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                          handlePageLoaded(url);
                                      }
                                  }
        );

        String url = String.format("%s?client_id=%s", OAUTH_URL, mClientId);
        mWebView.loadUrl(url);
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
     * Handles event of github success authentication
     *
     * @param event Event data
     */
    public void onEvent(GithubAuthSuccessEvent event) {
        mTaskManager.startAuth(event.getAccessToken());
    }

    /**
     * Handles event of github failed authentication
     *
     * @param event Event data
     */
    public void onEvent(GithubAuthFailEvent event) {
        // TODO Handle error
        Toast.makeText(this, event.getTaskError().getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles event of success authentication
     *
     * @param event Event data
     */
    public void onEvent(AuthSuccessEvent event) {
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
        // TODO Handle error
        Toast.makeText(this, event.getTaskError().getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Is called when authorization page loaded
     *
     * @param url Loaded URL
     */
    private void handlePageLoaded(String url) {
        String accessCodeFragment = "code=";

        if (url.contains(accessCodeFragment)) {
            // the GET request contains an authorization code
            String accessCode = url.substring(url.indexOf(accessCodeFragment) + accessCodeFragment.length());

            // TODO Start showing progress bar

            mTaskManager.startGithubAuth(mClientId, mClientSecret, accessCode);
        }
    }

}
