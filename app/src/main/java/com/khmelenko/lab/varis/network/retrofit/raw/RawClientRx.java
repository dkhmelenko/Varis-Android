package com.khmelenko.lab.varis.network.retrofit.raw;

import com.khmelenko.lab.varis.storage.AppSettings;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


/**
 * Raw http client
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class RawClientRx {

    private Retrofit mRetrofit;
    private final OkHttpClient mHttpClient;

    private RawApiServiceRx mRawApiService;

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
        mRetrofit = new Retrofit.Builder()
                .baseUrl(newEndpoint)
                .addConverterFactory(new Converter.Factory() {
                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                        if (String.class.equals(type)) {
                            return new Converter<ResponseBody, String>() {
                                @Override
                                public String convert(ResponseBody value) throws IOException {
                                    return value.string();
                                }
                            };
                        }
                        return null;
                    }
                })
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mHttpClient)
                .build();
        mRawApiService = mRetrofit.create(RawApiServiceRx.class);
    }

    /**
     * Gets Raw API service
     *
     * @return Raw API service
     */
    public RawApiServiceRx getApiService() {
        return mRawApiService;
    }

    /**
     * Executes single request
     *
     * @param url URL for request
     * @return Response
     */
    public Single<Response> singleRequest(String url) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        return Single.create(new SingleOnSubscribe<Response>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Response> e) throws Exception {
                Response response = mHttpClient.newCall(request).execute();
                e.onSuccess(response);
            }
        });
    }

    public String getLogUrl(Long jobId) {
        String logUrl = String.format("%sjobs/%d/log", mRetrofit.baseUrl(), jobId);
        return logUrl;
    }

}
