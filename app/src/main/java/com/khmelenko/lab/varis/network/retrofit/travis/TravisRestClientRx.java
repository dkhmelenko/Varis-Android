package com.khmelenko.lab.varis.network.retrofit.travis;

import android.text.TextUtils;

import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.util.PackageUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * REST client for Network communication
 *
 * @author Dmytro Khmelenko
 */
public class TravisRestClientRx {

    private final Retrofit mRetrofit;

    private final OkHttpClient mOkHttpClient;

    private TravisApiServiceRx mApiService;

    public TravisRestClientRx(Retrofit retrofit, OkHttpClient okHttpClient) {
        mRetrofit = retrofit;
        mOkHttpClient = okHttpClient;
        final String travisUrl = AppSettings.getServerUrl();
        updateTravisEndpoint(travisUrl);
    }

    /**
     * Updates Travis endpoint
     *
     * @param newEndpoint New endpoint
     */
    public void updateTravisEndpoint(String newEndpoint) {
        Retrofit retrofit = mRetrofit.newBuilder()
                .baseUrl(newEndpoint)
                .client(getHttpClient())
                .build();

        mApiService = retrofit.create(TravisApiServiceRx.class);
    }

    private OkHttpClient getHttpClient() {
        final String userAgent = String.format("TravisClient/%1$s", PackageUtils.getAppVersion());

        return mOkHttpClient.newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request.Builder request = original.newBuilder()
                                .header("User-Agent", userAgent)
                                .header("Accept", "application/vnd.travis-ci.2+json");

                        String accessToken = AppSettings.getAccessToken();
                        if (!TextUtils.isEmpty(accessToken)) {
                            String headerValue = String.format("token %1$s", accessToken);
                            request.addHeader("Authorization", headerValue);
                        }

                        return chain.proceed(request.build());
                    }
                })
                .build();
    }

    /**
     * Gets travis API service
     *
     * @return Travis API service
     */
    public TravisApiServiceRx getApiService() {
        return mApiService;
    }

}
