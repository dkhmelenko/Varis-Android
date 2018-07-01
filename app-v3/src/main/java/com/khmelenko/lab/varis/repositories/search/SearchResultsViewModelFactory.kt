package com.khmelenko.lab.varis.repositories.search

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import javax.inject.Inject

/**
 * Factory for creating an instance of [SearchResultsViewModel]
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class SearchResultsViewModelFactory @Inject constructor(
        private val travisRestClient: TravisRestClient
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = SearchResultsViewModel(travisRestClient) as T
}
