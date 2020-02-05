package com.khmelenko.lab.varis.builddetails

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.network.response.Job
import kotlinx.android.synthetic.main.fragment_jobs.jobs_recycler_view

/**
 * Fragment with build jobs
 *
 * @author Dmytro Khmelenko
 */
class JobsFragment : Fragment() {

    private lateinit var jobsListAdapter: JobsListAdapter
    private val jobs = ArrayList<Job>()

    private var listener: JobsListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_jobs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobs_recycler_view.isNestedScrollingEnabled = false
        jobs_recycler_view.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        jobs_recycler_view.layoutManager = layoutManager

        jobs.clear()
        jobs.addAll(readJobsFromArgs())

        jobsListAdapter = JobsListAdapter(context!!, jobs) { position ->
            if (!jobs.isEmpty()) {
                val job = jobs[position]
                listener!!.onJobSelected(job)
            }
        }
        jobs_recycler_view.adapter = jobsListAdapter

        val metrics = resources.displayMetrics
        val itemHeight = (jobsListAdapter.itemHeight * metrics.density + 0.5).toInt()
        jobs_recycler_view.layoutParams.height = itemHeight * jobsListAdapter.itemCount
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        try {
            listener = activity as JobsListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement JobsListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Sets the list of jobs
     *
     * @param jobs Jobs
     */
    fun setJobs(jobs: List<Job>) {
        this.jobs.clear()
        this.jobs.addAll(jobs)

        jobsListAdapter.notifyDataSetChanged()
    }

    private fun readJobsFromArgs(): List<Job> {
        val jobsAsString = arguments!!.getString(JOBS_KEY)
        val gson = Gson()
        val listType = object : TypeToken<List<Job>>() {}.type
        return gson.fromJson<List<Job>>(jobsAsString, listType)
    }

    /**
     * Interface for communication with this fragment
     */
    interface JobsListener {

        /**
         * Handles job selection
         *
         * @param job Selected job
         */
        fun onJobSelected(job: Job)
    }

    companion object {

        const val JOBS_KEY = "JOBS_KEY"

        /**
         * Creates new instance of the fragment
         *
         * @return Fragment instance
         */
        fun newInstance(jobs: List<Job>): JobsFragment {
            val fragment = JobsFragment()
            val args = Bundle()
            args.putString(JOBS_KEY, jobsToJson(jobs))
            fragment.arguments = args
            return fragment
        }

        private fun jobsToJson(jobs: List<Job>): String {
            val gson = Gson()
            return gson.toJson(jobs)
        }
    }

}
