package com.khmelenko.lab.varis.auth

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.common.Constants
import kotlinx.android.synthetic.main.fragment_auth.auth_login_btn
import kotlinx.android.synthetic.main.fragment_auth.auth_password
import kotlinx.android.synthetic.main.fragment_auth.auth_server_selector
import kotlinx.android.synthetic.main.fragment_auth.auth_username
import kotlinx.android.synthetic.main.fragment_auth.oauth_login_button
import kotlinx.android.synthetic.main.fragment_auth.oauth_token

/**
 * Authentication fragment
 *
 * @author Dmytro Khmelenko
 */
class AuthFragment : Fragment() {

    private var currentServer: String? = null

    private var listener: OnLoginActionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentServer = arguments!!.getString(SERVER_URL)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareServerSelection()

        auth_login_btn.setOnClickListener {
            if (areCredentialsValid()) {
                listener?.onLogin(auth_username.text.toString(), auth_password.text.toString())
            }
        }

        oauth_login_button.setOnClickListener {
            if (areOauthLoginFieldsValid()) {
                listener?.onOauthLogin(oauth_token.text.toString())
            }
        }
    }

    /**
     * Prepares server selection section
     */
    private fun prepareServerSelection() {
        auth_server_selector.setOnCheckedChangeListener { radioGroup, buttonId ->
            var server = Constants.OPEN_SOURCE_TRAVIS_URL
            when (buttonId) {
                R.id.auth_server_opensource -> server = Constants.OPEN_SOURCE_TRAVIS_URL
                R.id.auth_server_pro -> server = Constants.PRIVATE_TRAVIS_URL
            }

            listener?.onChangeServer(server)
        }

        when (currentServer) {
            Constants.OPEN_SOURCE_TRAVIS_URL -> auth_server_selector.check(R.id.auth_server_opensource)
            Constants.PRIVATE_TRAVIS_URL -> auth_server_selector.check(R.id.auth_server_pro)
        }
    }

    private fun areOauthLoginFieldsValid(): Boolean {
        var valid = true
        if (TextUtils.isEmpty(oauth_token.text)) {
            valid = false
            oauth_token.error = getString(R.string.auth_invalid_token_msg)
        }
        return valid
    }

    /**
     * Does validation of the inputted credentials
     *
     * @return True, if inputted credentials are valid. False otherwise
     */
    private fun areCredentialsValid(): Boolean {
        var valid = true
        if (TextUtils.isEmpty(auth_username.text)) {
            valid = false
            auth_username.error = getString(R.string.auth_invalid_username_msg)
        }

        if (TextUtils.isEmpty(auth_password.text)) {
            valid = false
            auth_password.error = getString(R.string.auth_invalid_password_msg)
        }
        return valid
    }

    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        try {
            listener = activity as? OnLoginActionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement OnLoginActionListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Interface for fragment actions listener
     */
    interface OnLoginActionListener {

        /**
         * Called on Login action
         *
         * @param userName Username
         * @param password Password
         */
        fun onLogin(userName: String, password: String)

        /**
         * Called on changed server
         *
         * @param newServer New server URL
         */
        fun onChangeServer(newServer: String)

        /**
         * Called when Logging in using API Token
         * @param userName Username
         */
        fun onOauthLogin(token: String)
    }

    companion object {

        private const val SERVER_URL = "ServerUrl"

        /**
         * Creates new instance of the fragment
         *
         * @return Fragment instance
         */
        fun newInstance(serverUrl: String): AuthFragment {
            val fragment = AuthFragment()
            val bundle = Bundle()
            bundle.putString(SERVER_URL, serverUrl)
            fragment.arguments = bundle
            return fragment
        }
    }
}
