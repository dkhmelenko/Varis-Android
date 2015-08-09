package com.khmelenko.lab.travisclient.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.event.github.AuthFailEvent;
import com.khmelenko.lab.travisclient.event.github.AuthSuccessEvent;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.util.AssetsUtils;

import java.util.Properties;

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

    private EventBus mEventBus = EventBus.getDefault();

    // TODO Move to DI
    private TaskManager mTaskManager = new TaskManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // load required properties
        Properties properties = AssetsUtils.getProperties(Constants.KEY_PROPERTIES, this);
        mClientId = properties.getProperty("clientId");
        mClientSecret = properties.getProperty("clientSecret");

        WebView webview = (WebView) findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
                                     public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                         handlePageLoaded(url);
                                     }
                                 }
        );

        String url = String.format("%s?client_id=%s", OAUTH_URL, mClientId);
        webview.loadUrl(url);
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
