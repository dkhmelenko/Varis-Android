package com.khmelenko.lab.varis.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.common.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Authentication fragment
 *
 * @author Dmytro Khmelenko
 */
public class AuthFragment extends Fragment {

    private static final String SERVER_URL = "ServerUrl";

    @BindView(R.id.auth_server_selector)
    RadioGroup mServerSelector;

    @BindView(R.id.auth_username)
    EditText mUsername;

    @BindView(R.id.auth_password)
    EditText mPassword;

    private String mCurrentServer;

    private OnLoginActionListener mListener;

    /**
     * Creates new instance of the fragment
     *
     * @return Fragment instance
     */
    public static AuthFragment newInstance(String serverUrl) {
        AuthFragment fragment = new AuthFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SERVER_URL, serverUrl);
        fragment.setArguments(bundle);
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

        prepareServerSelection();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mCurrentServer = args.getString(SERVER_URL);
    }

    /**
     * Prepares server selection section
     */
    private void prepareServerSelection() {
        mServerSelector.setOnCheckedChangeListener((radioGroup, buttonId) -> {
            String server = Constants.OPEN_SOURCE_TRAVIS_URL;
            switch (buttonId) {
                case R.id.auth_server_opensource:
                    server = Constants.OPEN_SOURCE_TRAVIS_URL;
                    break;
                case R.id.auth_server_pro:
                    server = Constants.PRIVATE_TRAVIS_URL;
                    break;
            }

            mListener.onChangeServer(server);
        });

        switch (mCurrentServer) {
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
    public void onAttach(Context activity) {
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

        /**
         * Called on changed server
         *
         * @param newServer New server URL
         */
        void onChangeServer(String newServer);
    }

}
