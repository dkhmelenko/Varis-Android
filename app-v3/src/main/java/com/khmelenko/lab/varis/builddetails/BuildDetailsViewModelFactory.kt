package com.khmelenko.lab.varis.builddetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khmelenko.lab.varis.log.LogsParser
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.storage.CacheStorage
import javax.inject.Inject

/**
 * Factory for creating an instance of [BuildsDetailsViewModel]
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class BuildDetailsViewModelFactory @Inject constructor(
        private val travisRestClient: TravisRestClient,
        private val rawClient: RawClient,
        private val cache: CacheStorage,
        private val appSettings: AppSettings,
        private val logsParser: LogsParser
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BuildsDetailsViewModel(travisRestClient, rawClient, cache, appSettings, logsParser) as T
    }
}
