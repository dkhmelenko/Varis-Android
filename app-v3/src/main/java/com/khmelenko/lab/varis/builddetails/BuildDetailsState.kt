package com.khmelenko.lab.varis.builddetails

import com.khmelenko.lab.varis.log.LogEntryComponent
import com.khmelenko.lab.varis.network.response.BuildDetails
import com.khmelenko.lab.varis.network.response.Job

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
sealed class BuildDetailsState {

    object Loading : BuildDetailsState()
    object LogError : BuildDetailsState()
    data class Error(val message: String?) : BuildDetailsState()
    data class BuildDetailsLoaded(val details: BuildDetails) : BuildDetailsState()
    data class BuildJobsLoaded(val jobs: List<Job>) : BuildDetailsState()
    data class LogEntryLoaded(val logEntry: LogEntryComponent) : BuildDetailsState()
    object BuildLogsLoaded : BuildDetailsState()
    data class BuildAdditionalActionsAvailable(val details: BuildDetails) : BuildDetailsState()
}
