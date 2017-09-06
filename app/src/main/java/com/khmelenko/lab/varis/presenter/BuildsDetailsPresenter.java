package com.khmelenko.lab.varis.presenter;

import com.khmelenko.lab.varis.log.LogsParser;
import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.network.response.BuildDetails;
import com.khmelenko.lab.varis.network.response.Job;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.storage.CacheStorage;
import com.khmelenko.lab.varis.util.StringUtils;
import com.khmelenko.lab.varis.view.BuildDetailsView;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.HttpException;

/**
 * Presenter for BuildDetails
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class BuildsDetailsPresenter extends MvpPresenter<BuildDetailsView> {

    public static final int LOAD_LOG_MAX_ATTEMPT = 3;

    private final TravisRestClient mTravisRestClient;
    private final RawClient mRawClient;
    private final CacheStorage mCache;
    private final AppSettings mAppSettings;
    private final LogsParser mLogsParser;

    private final CompositeDisposable mSubscriptions;

    private String mRepoSlug = "";
    private long mBuildId;
    private long mJobId;

    private int mLoadLogAttempt = 0;

    @Inject
    public BuildsDetailsPresenter(TravisRestClient travisRestClient, RawClient rawClient, CacheStorage cache,
                                  AppSettings appSettings, LogsParser logsParser) {
        mTravisRestClient = travisRestClient;
        mRawClient = rawClient;
        mCache = cache;
        mAppSettings = appSettings;
        mLogsParser = logsParser;

        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void onAttach() {
        // do nothing
    }

    @Override
    public void onDetach() {
        mSubscriptions.clear();
    }

    /**
     * Starts loading log file
     *
     * @param jobId Job ID
     */
    public void startLoadingLog(long jobId) {
        mJobId = jobId;
        String accessToken = mAppSettings.getAccessToken();
        Single<String> responseSingle;
        if (StringUtils.isEmpty(accessToken)) {
            responseSingle = mRawClient.getApiService().getLog(String.valueOf(mJobId));
        } else {
            String auth = String.format("token %1$s", mAppSettings.getAccessToken());
            responseSingle = mRawClient.getApiService().getLog(auth, String.valueOf(mJobId));
        }

        Disposable subscription = responseSingle.subscribeOn(Schedulers.io())
                .map(s -> mRawClient.getLogUrl(mJobId))
                .onErrorResumeNext(new Function<Throwable, SingleSource<String>>() {
                    @Override
                    public SingleSource<String> apply(@NonNull Throwable throwable) throws Exception {
                        String redirectUrl = "";
                        if (throwable instanceof HttpException) {
                            HttpException httpException = (HttpException) throwable;
                            Headers headers = httpException.response().headers();
                            for (String header : headers.names()) {
                                if (header.equals("Location")) {
                                    redirectUrl = headers.get(header);
                                    break;
                                }
                            }
                            return Single.just(redirectUrl);
                        } else {
                            return Single.error(throwable);
                        }
                    }
                })
                .retry(LOAD_LOG_MAX_ATTEMPT)
                .map(mRawClient::singleStringRequest)
                .map(response -> mLogsParser.parseLog(response.blockingGet()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((log, throwable) -> {
                    if (throwable == null) {
                        getView().setLog(log);
                    } else {
                        getView().showLogError();
                        getView().showLoadingError(throwable.getMessage());
                    }
                });

        mSubscriptions.add(subscription);
    }

    /**
     * Starts loading data
     *
     * @param intentUrl Intent URL
     * @param repoSlug  Repository slug
     * @param buildId   Build ID
     */
    public void startLoadingData(String intentUrl, String repoSlug, long buildId) {
        mRepoSlug = repoSlug;
        mBuildId = buildId;

        Single<BuildDetails> buildDetailsSingle;

        if (!StringUtils.isEmpty(intentUrl)) {
            buildDetailsSingle = mRawClient.singleRequest(intentUrl)
                    .doOnSuccess(response -> {
                        String redirectUrl = intentUrl;
                        if (response.isRedirect()) {
                            redirectUrl = response.header("Location", "");
                        }
                        parseIntentUrl(redirectUrl);
                    })
                    .flatMap(new Function<okhttp3.Response, SingleSource<BuildDetails>>() {
                        @Override
                        public SingleSource<BuildDetails> apply(@NonNull okhttp3.Response response) throws Exception {
                            return mTravisRestClient.getApiService().getBuild(mRepoSlug, mBuildId);
                        }
                    });
        } else {
            buildDetailsSingle = mTravisRestClient.getApiService().getBuild(mRepoSlug, mBuildId);
        }

        Disposable subscription = buildDetailsSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((buildDetails, throwable) -> {
                    if (throwable == null) {
                        handleBuildDetails(buildDetails);
                    } else {
                        handleLoadingFailed(throwable);
                    }
                });

        mSubscriptions.add(subscription);

        getView().showProgress();
    }

    /**
     * Parses intent URL
     *
     * @param intentUrl Intent URL
     */
    private void parseIntentUrl(String intentUrl) {
        final int ownerIndex = 1;
        final int repoNameIndex = 2;
        final int buildIdIndex = 4;
        final int pathLength = 5;

        try {
            URL url = new URL(intentUrl);
            String path = url.getPath();
            String[] items = path.split("/");
            if (items.length >= pathLength) {
                mRepoSlug = String.format("%s/%s", items[ownerIndex], items[repoNameIndex]);
                mBuildId = Long.valueOf(items[buildIdIndex]);
            }
        } catch (MalformedURLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restarts build process
     */
    public void restartBuild() {
        RequestBody emptyBody = RequestBody.create(MediaType.parse("application/json"), "");
        Disposable subscription = mTravisRestClient.getApiService()
                .restartBuild(mBuildId, emptyBody)
                .onErrorReturn(throwable -> new Object())
                .flatMap(new Function<Object, SingleSource<BuildDetails>>() {
                    @Override
                    public SingleSource<BuildDetails> apply(@NonNull Object o) throws Exception {
                        return mTravisRestClient.getApiService().getBuild(mRepoSlug, mBuildId);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((buildDetails, throwable) -> {
                    if (throwable == null) {
                        handleBuildDetails(buildDetails);
                    } else {
                        handleLoadingFailed(throwable);
                    }
                });

        mSubscriptions.add(subscription);
    }

    /**
     * Cancels build process
     */
    public void cancelBuild() {
        RequestBody emptyBody = RequestBody.create(MediaType.parse("application/json"), "");
        Disposable subscription = mTravisRestClient.getApiService()
                .cancelBuild(mBuildId, emptyBody)
                .onErrorReturn(throwable -> new Object())
                .flatMap(new Function<Object, SingleSource<BuildDetails>>() {
                    @Override
                    public SingleSource<BuildDetails> apply(@NonNull Object o) throws Exception {
                        return mTravisRestClient.getApiService().getBuild(mRepoSlug, mBuildId);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((buildDetails, throwable) -> {
                    if (throwable == null) {
                        handleBuildDetails(buildDetails);
                    } else {
                        handleLoadingFailed(throwable);
                    }
                });

        mSubscriptions.add(subscription);
    }

    /**
     * Defines whether the user can contribute to the repository or not
     *
     * @return True if user can contribute to the repository. False otherwise
     */
    public boolean canUserContributeToRepo() {
        boolean canContributeToRepo = false;
        String[] userRepos = mCache.restoreRepos();
        for (String repo : userRepos) {
            if (repo.equals(mRepoSlug)) {
                canContributeToRepo = true;
                break;
            }
        }
        return canContributeToRepo;
    }

    private void handleBuildDetails(BuildDetails buildDetails) {
        getView().hideProgress();
        getView().updateBuildDetails(buildDetails);

        if (buildDetails != null) {
            if (buildDetails.getJobs().size() > 1) {
                getView().showBuildJobs(buildDetails.getJobs());
            } else if (buildDetails.getJobs().size() == 1) {
                getView().showBuildLogs();

                Job job = buildDetails.getJobs().get(0);
                startLoadingLog(job.getId());
            }

            // if user logged in, show additional actions for the repo
            String appToken = mAppSettings.getAccessToken();
            if (!StringUtils.isEmpty(appToken)) {
                getView().showAdditionalActionsForBuild(buildDetails);
            }
        }
    }

    private void handleLoadingFailed(Throwable throwable) {
        getView().hideProgress();
        getView().updateBuildDetails(null);
        getView().showLoadingError(throwable.getMessage());
    }
}
