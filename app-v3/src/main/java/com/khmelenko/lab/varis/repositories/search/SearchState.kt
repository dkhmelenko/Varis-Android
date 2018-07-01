package com.khmelenko.lab.varis.repositories.search

import com.khmelenko.lab.varis.network.response.Repo
import com.khmelenko.lab.varis.network.response.User
import com.khmelenko.lab.varis.repositories.RepositoriesState

sealed class SearchState {

    object Loading : SearchState()
    data class Error(val message: String?) : SearchState()
    data class ReposList(val repos: List<Repo>) : SearchState()
}
