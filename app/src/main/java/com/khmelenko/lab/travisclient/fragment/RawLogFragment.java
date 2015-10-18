package com.khmelenko.lab.travisclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.storage.AppSettings;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Shows raw logs
 *
 * @author Dmytro Khmelenko
 */
public class RawLogFragment extends Fragment {

    private static final String JOB_ID_KEY = "JobId";

    private static final String RAW_LOG_PATH = "/jobs/%1$s/log";

    @Bind(R.id.raw_log_webview)
    WebView mWebView;

    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    private long mJobId;
    private OnRawLogFragmentListener mListener;

    /**
     * Creates an instance of the fragment
     *
     * @param jobId Job ID
     * @return Fragment instance
     */
    public static RawLogFragment newInstance(long jobId) {
        RawLogFragment fragment = new RawLogFragment();
        Bundle args = new Bundle();
        args.putLong(JOB_ID_KEY, jobId);
        fragment.setArguments(args);
        return fragment;
    }

    public RawLogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mJobId = getArguments().getLong(JOB_ID_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_raw_log, container, false);
        ButterKnife.bind(this, view);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mListener != null) {
                    mListener.onLogLoaded();
                }

                mProgressBar.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
            }
        });

        String path = formatLogPath();
        mWebView.loadUrl(path);

        return view;
    }

    /**
     * Formats the path to the job logs
     *
     * @return Path to logs
     */
    private String formatLogPath() {
        String server = AppSettings.getServerUrl();
        String path = String.format(RAW_LOG_PATH, String.valueOf(mJobId));
        return server + path;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRawLogFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRawLogFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interface for communication with this fragment
     */
    public interface OnRawLogFragmentListener {

        /**
         * Raised when raw log loaded
         */
        void onLogLoaded();
    }

}
