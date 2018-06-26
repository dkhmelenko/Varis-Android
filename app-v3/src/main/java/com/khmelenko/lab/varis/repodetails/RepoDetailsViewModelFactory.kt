package com.khmelenko.lab.varis.repodetails

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import javax.inject.Inject

/**
 * The factory for creating an instance of [RepoDetailsViewModel]
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class RepoDetailsViewModelFactory @Inject constructor(
        private val travisRestClient: TravisRestClient
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RepoDetailsViewModel(travisRestClient) as T
    }
}
