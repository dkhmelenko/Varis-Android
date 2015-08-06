package com.khmelenko.lab.travisclient.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.util.AssetsUtils;

import java.util.Properties;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Authentication activity
 *
 * @author Dmytro Khmelenko
 */
public class AuthActivity extends AppCompatActivity {

    public static final String OAUTH_URL = "https://github.com/login/oauth/authorize";

    private String mClientId;
    private String mClientSecret;

    private RestClient mRestClient = new RestClient();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Properties properties = AssetsUtils.getProperties(Constants.KEY_PROPERTIES, this);
        mClientId = properties.getProperty("clientId");
        mClientSecret = properties.getProperty("clientSecret");

        String url = String.format("%s?client_id=%s", OAUTH_URL, mClientId);

        WebView webview = (WebView) findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
                                     public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                         handlePageLoaded(url);
                                     }
                                 }

        );
        webview.loadUrl(url);

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

            mRestClient.getApiService().accesToken(mClientId, mClientSecret, accessCode,
                    new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {
                            Log.d("Travis", "Response: " + s);
                            // TODO Store received access_token
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Travis", "Error: " + error.toString());
                            // TODO Handle error
                        }
                    });
        }
    }

}
