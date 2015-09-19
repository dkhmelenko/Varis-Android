package com.khmelenko.lab.travisclient.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.event.github.GithubAuthorizationFailEvent;
import com.khmelenko.lab.travisclient.event.travis.AuthFailEvent;
import com.khmelenko.lab.travisclient.event.travis.AuthSuccessEvent;
import com.khmelenko.lab.travisclient.task.TaskManager;

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
     * Handles event of github failed authentication
     *
     * @param event Event data
     */
    public void onEvent(GithubAuthorizationFailEvent event) {
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


}
