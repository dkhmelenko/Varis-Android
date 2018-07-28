package com.khmelenko.lab.varis.auth

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.storage.AppSettings
import javax.inject.Inject

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class AuthViewModelFactory @Inject constructor(private val travisRestClient: TravisRestClient,
                                               private val gitHubRestClient: GitHubRestClient,
                                               private val appSettings: AppSettings
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(travisRestClient, gitHubRestClient, appSettings) as T
    }
}
