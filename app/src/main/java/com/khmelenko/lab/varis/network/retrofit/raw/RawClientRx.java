package com.khmelenko.lab.varis.network.retrofit.raw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khmelenko.lab.varis.network.retrofit.ItemTypeAdapterFactory;
import com.khmelenko.lab.varis.storage.AppSettings;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Raw http client
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class RawClientRx {

    private RawApiService mRawApiService;

    private OkHttpClient mHttpClient;

    private RawClientRx() {
        initHttpClient();

        final String travisUrl = AppSettings.getServerUrl();
        updateEndpoint(travisUrl);
    }

    /**
     * Initializes HTTP client
     */
    private void initHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .followRedirects(false)
                .build();
    }

    /**
     * Creates new instance of the rest client
     *
     * @return Instance
     */
    public static RawClientRx newInstance() {
        return new RawClientRx();
    }

    /**
     * Updates Travis endpoint
     *
     * @param newEndpoint New endpoint
     */
    public void updateEndpoint(String newEndpoint) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(newEndpoint)
                .addConverterFactory(GsonConverterFactory.create(constructGsonConverter()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mHttpClient)
                .build();
        mRawApiService = retrofit.create(RawApiService.class);
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

        Request request = new Request.Builder()
                .url(url)
                .build();

        return mHttpClient.newCall(request).execute();
    }

}
