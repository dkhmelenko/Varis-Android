package com.khmelenko.lab.varis.auth

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.activity.BaseActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

private const val AUTH_FRAGMENT_TAG = "AuthFragment"
private const val SECURITY_CODE_FRAGMENT_TAG = "SecurityCodeFragment"

/**
 * Authentication activity
 *
 * @author Dmytro Khmelenko
 */
class AuthActivity : BaseActivity(), AuthFragment.OnLoginActionListener, SecurityCodeFragment.OnSecurityCodeAction {

    private var progressDialog: ProgressDialog? = null

    @Inject
    lateinit var viewModelFactory: AuthViewModelFactory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        initToolbar()

        viewModel = ViewModelProvider(this, viewModelFactory).get(AuthViewModel::class.java)
        viewModel.state().observe(this, Observer {
            handleState(it!!)
        })

        setInputView(viewModel.isCodeInput)
    }

    private fun handleState(state: AuthState) {
        hideProgress()
        when (state) {
            is AuthState.Loading -> showProgress()
            is AuthState.Success -> finishView()
            is AuthState.ShowTwoFactorAuth -> showTwoFactorAuth()
            is AuthState.AuthError -> showErrorMessage(state.message)
        }
    }

    /**
     * Shows login section
     */
    private fun showLoginSection() {
        var authFragment: AuthFragment? = supportFragmentManager.findFragmentByTag(AUTH_FRAGMENT_TAG) as? AuthFragment
        if (authFragment == null) {
            authFragment = AuthFragment.newInstance(viewModel.serverUrl)
            addFragment(R.id.auth_container, authFragment, AUTH_FRAGMENT_TAG)
        }
    }

    /**
     * Shows the input for security code
     */
    private fun showSecurityCodeInput() {
        var securityCodeFragment: SecurityCodeFragment? = supportFragmentManager.findFragmentByTag(SECURITY_CODE_FRAGMENT_TAG) as? SecurityCodeFragment
        if (securityCodeFragment == null) {
            securityCodeFragment = SecurityCodeFragment.newInstance()
            replaceFragment(R.id.auth_container, securityCodeFragment, SECURITY_CODE_FRAGMENT_TAG)
        }
    }

    /**
     * Initializes toolbar
     */
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { v -> onBackPressed() }
    }

    override fun onLogin(userName: String, password: String) {
        viewModel.login(userName, password)
    }

    override fun onChangeServer(newServer: String) {
        viewModel.updateServer(newServer)
    }

    override fun onOauthLogin(token: String) {
        viewModel.oauthLogin(token)
    }

    override fun onSecurityCodeInput(securityCode: String) {
        viewModel.twoFactorAuth(securityCode)
    }

    private fun showProgress() {
        progressDialog = ProgressDialog.show(this, "", getString(R.string.loading_msg))
    }

    private fun hideProgress() {
        progressDialog?.dismiss()
    }

    private fun finishView() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showErrorMessage(message: String?) {
        val msg = getString(R.string.error_failed_auth, message)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun showTwoFactorAuth() {
        showSecurityCodeInput()
    }

    private fun setInputView(securityCodeInput: Boolean) {
        if (securityCodeInput) {
            showSecurityCodeInput()
        } else {
            showLoginSection()
        }
    }
}
