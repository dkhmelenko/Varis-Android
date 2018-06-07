package com.khmelenko.lab.varis.repositories

import com.khmelenko.lab.varis.network.response.Repo
import com.khmelenko.lab.varis.network.response.User

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
sealed class RepositoriesState {

    object Loading : RepositoriesState()
    data class Error(val message: String?) : RepositoriesState()
    data class ReposList(val repos: List<Repo>) : RepositoriesState()
    data class UserData(val user: User?) : RepositoriesState()
}
