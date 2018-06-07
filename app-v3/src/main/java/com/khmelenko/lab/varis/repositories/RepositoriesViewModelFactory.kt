package com.khmelenko.lab.varis.repositories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.storage.CacheStorage
import javax.inject.Inject

/**
 * Factory for creating an instance of [RepositoriesViewModel]
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class RepositoriesViewModelFactory @Inject constructor(
        val restClient: TravisRestClient,
        val storage: CacheStorage,
        val appSettings: AppSettings
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RepositoriesViewModel(restClient, storage, appSettings) as T
    }
}
