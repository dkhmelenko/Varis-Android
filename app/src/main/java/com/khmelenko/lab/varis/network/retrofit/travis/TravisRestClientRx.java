package com.khmelenko.lab.varis.network.retrofit.travis;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khmelenko.lab.varis.network.retrofit.ItemTypeAdapterFactory;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.util.PackageUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * REST client for Network communication
 *
 * @author Dmytro Khmelenko
 */
public class TravisRestClientRx {

    private TravisApiServiceRx mApiService;

    private TravisRestClientRx() {
        final String travisUrl = AppSettings.getServerUrl();
        updateTravisEndpoint(travisUrl);
    }

    /**
     * Creates new instance of the rest client
     *
     * @return Instance
     */
    public static TravisRestClientRx newInstance() {
        return new TravisRestClientRx();
    }

    /**
     * Updates Travis endpoint
     *
     * @param newEndpoint New endpoint
     */
    public void updateTravisEndpoint(String newEndpoint) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(newEndpoint)
                .addConverterFactory(GsonConverterFactory.create(constructGsonConverter()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getHttpClient())
                .build();

        mApiService = retrofit.create(TravisApiServiceRx.class);
    }

    private OkHttpClient getHttpClient() {
        final String userAgent = String.format("TravisClient/%1$s", PackageUtils.getAppVersion());

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
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
        });

        return httpClient.build();
    }

    /**
     * Construct Gson converter
     *
     * @return Gson converter
     */
    private Gson constructGsonConverter() {
        return new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();
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
