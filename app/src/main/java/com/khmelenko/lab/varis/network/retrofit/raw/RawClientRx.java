package com.khmelenko.lab.varis.network.retrofit.raw;

import com.khmelenko.lab.varis.storage.AppSettings;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;


/**
 * Raw http client
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class RawClientRx {

    private final Retrofit mRetrofit;
    private final OkHttpClient mHttpClient;

    private RawApiService mRawApiService;

    public RawClientRx(Retrofit retrofit, OkHttpClient okHttpClient) {
        mRetrofit = retrofit;
        mHttpClient = okHttpClient;

        final String travisUrl = AppSettings.getServerUrl();
        updateEndpoint(travisUrl);
    }

    /**
     * Updates Travis endpoint
     *
     * @param newEndpoint New endpoint
     */
    public void updateEndpoint(String newEndpoint) {
        Retrofit retrofit = mRetrofit.newBuilder()
                .baseUrl(newEndpoint)
                .build();
        mRawApiService = retrofit.create(RawApiService.class);
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
