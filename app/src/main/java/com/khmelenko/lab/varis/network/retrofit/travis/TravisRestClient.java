package com.khmelenko.lab.varis.network.retrofit.travis;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khmelenko.lab.varis.network.retrofit.ItemTypeAdapterFactory;
import com.khmelenko.lab.varis.network.retrofit.RestErrorHandling;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.util.PackageUtils;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * REST client for Network communication
 *
 * @author Dmytro Khmelenko
 */
public class TravisRestClient {

    private TravisApiService mApiService;

    private TravisRestClient() {
        final String travisUrl = AppSettings.getServerUrl();
        updateTravisEndpoint(travisUrl);
    }

    /**
     * Creates new instance of the rest client
     *
     * @return Instance
     */
    public static TravisRestClient newInstance() {
        return new TravisRestClient();
    }

    /**
     * Updates Travis endpoint
     *
     * @param newEndpoint New endpoint
     */
    public void updateTravisEndpoint(String newEndpoint) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(newEndpoint)
                .setConverter(new GsonConverter(constructGsonConverter()))
                .setRequestInterceptor(constructRequestInterceptor())
                .setErrorHandler(new RestErrorHandling())
                .build();
        mApiService = restAdapter.create(TravisApiService.class);
    }

    /**
     * Constructs request interceptor
     *
     * @return Request interceptor
     */
    private RequestInterceptor constructRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                String userAgent = String.format("Varis/%1$s", PackageUtils.getAppVersion());
                request.addHeader("User-Agent", userAgent);
                request.addHeader("Accept", "application/vnd.travis-ci.2+json");

                String accessToken = AppSettings.getAccessToken();
                if (!TextUtils.isEmpty(accessToken)) {
                    String headerValue = String.format("token %1$s", accessToken);
                    request.addHeader("Authorization", headerValue);
                }
            }
        };
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
    public TravisApiService getApiService() {
        return mApiService;
    }

}
