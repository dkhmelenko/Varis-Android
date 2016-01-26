package com.khmelenko.lab.travisclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.TravisApp;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.storage.AppSettings;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Authentication fragment
 *
 * @author Dmytro Khmelenko
 */
public class AuthFragment extends Fragment {

    @Bind(R.id.auth_server_selector)
    RadioGroup mServerSelector;

    @Bind(R.id.auth_username)
    EditText mUsername;

    @Bind(R.id.auth_password)
    EditText mPassword;

    @Bind(R.id.auth_login_btn)
    Button mLoginBtn;

    private OnLoginActionListener mListener;

    @Inject
    RestClient mRestClient;

    /**
     * Creates new instance of the fragment
     *
     * @return Fragment instance
     */
    public static AuthFragment newInstance() {
        AuthFragment fragment = new AuthFragment();
        return fragment;
    }

    public AuthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        ButterKnife.bind(this, view);
        ((TravisApp) getActivity().getApplication()).getNetworkComponent().inject(this);

        prepareServerSelection();

        return view;
    }

    /**
     * Prepares server selection section
     */
    private void prepareServerSelection() {
        mServerSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int buttonId) {
                String server = Constants.OPEN_SOURCE_TRAVIS_URL;
                switch (buttonId) {
                    case R.id.auth_server_opensource:
                        server = Constants.OPEN_SOURCE_TRAVIS_URL;
                        break;
                    case R.id.auth_server_pro:
                        server = Constants.PRIVATE_TRAVIS_URL;
                        break;
                }

                AppSettings.putServerUrl(server);
                mRestClient.updateTravisEndpoint(server);
            }
        });

        String currentServer = AppSettings.getServerUrl();
        switch (currentServer) {
            case Constants.OPEN_SOURCE_TRAVIS_URL:
                mServerSelector.check(R.id.auth_server_opensource);
                break;
            case Constants.PRIVATE_TRAVIS_URL:
                mServerSelector.check(R.id.auth_server_pro);
                break;
        }
    }

    @OnClick(R.id.auth_login_btn)
    public void startAuthorization() {
        if (areCredentialsValid()) {
            if (mListener != null) {
                mListener.onLogin(mUsername.getText().toString(), mPassword.getText().toString());
            }
        }
    }

    /**
     * Does validation of the inputted credentials
     *
     * @return True, if inputted credentials are valid. False otherwise
     */
    private boolean areCredentialsValid() {
        boolean valid = true;
        if (TextUtils.isEmpty(mUsername.getText())) {
            valid = false;
            mUsername.setError(getString(R.string.auth_invalid_username_msg));
        }

        if (TextUtils.isEmpty(mPassword.getText())) {
            valid = false;
            mPassword.setError(getString(R.string.auth_invalid_password_msg));
        }
        return valid;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoginActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interface for fragment actions listener
     */
    public interface OnLoginActionListener {

        /**
         * Called on Login action
         *
         * @param userName Username
         * @param password Password
         */
        void onLogin(String userName, String password);
    }

}
