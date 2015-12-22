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
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Shows raw logs
 *
 * @author Dmytro Khmelenko
 */
public class RawLogFragment extends Fragment {

    private static final String LOG_URL = "LogUrl";

    @Bind(R.id.raw_log_webview)
    WebView mWebView;

    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    @Bind(R.id.empty_text)
    TextView mEmptyText;

    private OnRawLogFragmentListener mListener;

    /**
     * Creates an instance of the fragment
     *
     * @return Fragment instance
     */
    public static RawLogFragment newInstance() {
        RawLogFragment fragment = new RawLogFragment();
        return fragment;
    }

    public RawLogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_raw_log, container, false);
        ButterKnife.bind(this, view);
        showProgress(true);
        showError(false);
        return view;
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
     * Shows the loading progress of the fragment
     *
     * @param inProgress True, if the progress should be shown. False otherwise
     */
    public void showProgress(boolean inProgress) {
        if (inProgress) {
            mProgressBar.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows the error message
     *
     * @param isError True, if error text should be shown. False otherwise
     */
    public void showError(boolean isError) {
        if (isError) {
            mEmptyText.setVisibility(View.VISIBLE);
            mEmptyText.setText(R.string.build_details_empty);
        } else {
            mEmptyText.setVisibility(View.GONE);
        }
    }

    /**
     * Loads URL for showing in web view
     *
     * @param url URL
     */
    public void loadUrl(String url) {
        showError(false);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mListener != null) {
                    mListener.onLogLoaded();
                }

                showProgress(false);
            }
        });

        mWebView.loadUrl(url);
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
