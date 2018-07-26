package com.khmelenko.lab.varis.builddetails

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private val jobs = mutableListOf<Job>()

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

        jobsListAdapter = JobsListAdapter(context, jobs) { position ->
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


    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        try {
            listener = activity as JobsListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement JobsListener")
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

        /**
         * Creates new instance of the fragment
         *
         * @return Fragment instance
         */
        fun newInstance(): JobsFragment {
            val fragment = JobsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}
