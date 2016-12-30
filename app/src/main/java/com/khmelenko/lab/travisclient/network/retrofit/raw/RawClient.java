package com.khmelenko.lab.travisclient.network.retrofit.raw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khmelenko.lab.travisclient.network.retrofit.ItemTypeAdapterFactory;
import com.khmelenko.lab.travisclient.network.retrofit.RestErrorHandling;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Raw http client
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class RawClient {

    private RawApiService mRawApiService;

    private OkHttpClient mHttpClient;

    private RawClient() {
        initHttpClient();

        final String travisUrl = AppSettings.getServerUrl();
        updateEndpoint(travisUrl);
    }

    /**
     * Initializes HTTP client
     */
    private void initHttpClient() {
        mHttpClient = new OkHttpClient();
        mHttpClient.setFollowRedirects(false);
    }

    /**
     * Creates new instance of the rest client
     *
     * @return Instance
     */
    public static RawClient newInstance() {
        return new RawClient();
    }

    /**
     * Updates Travis endpoint
     *
     * @param newEndpoint New endpoint
     */
    public void updateEndpoint(String newEndpoint) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(newEndpoint)
                .setConverter(new GsonConverter(constructGsonConverter()))
                .setErrorHandler(new RestErrorHandling())
                .setClient(new OkClient(mHttpClient))
                .build();
        mRawApiService = restAdapter.create(RawApiService.class);
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
     * Gets Raw API service
     *
     * @return Raw API service
     */
    public RawApiService getApiService() {
        return mRawApiService;
    }

    /**
     * Executes single request
     *
     * @param url URL for request
     * @return Response
     */
    public Response singleRequest(String url) throws IOException {

        mHttpClient.setFollowRedirects(false);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mHttpClient.newCall(request).execute();
        return response;
    }

}
