package com.khmelenko.lab.varis.repodetails

import com.khmelenko.lab.varis.network.response.Branches
import com.khmelenko.lab.varis.network.response.BuildHistory
import com.khmelenko.lab.varis.network.response.Requests

sealed class RepoDetailsState {

    data class BuildHistoryLoaded(val buildHistory: BuildHistory) : RepoDetailsState()
    data class BuildHistoryError(val message: String?) : RepoDetailsState()
    data class PullRequestsLoaded(val pullRequests: Requests) : RepoDetailsState()
    data class PullRequestsError(val message: String?) : RepoDetailsState()
    data class BranchesLoaded(val branches: Branches) : RepoDetailsState()
    data class BranchesError(val message: String?) : RepoDetailsState()
}
