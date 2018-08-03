package com.khmelenko.lab.varis.builddetails

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.converter.BuildStateHelper
import com.khmelenko.lab.varis.converter.TimeConverter
import com.khmelenko.lab.varis.network.response.Job
import com.khmelenko.lab.varis.util.DateTimeUtils

import butterknife.ButterKnife
import kotlinx.android.synthetic.main.item_job.view.card_view
import kotlinx.android.synthetic.main.item_job.view.item_job_duration
import kotlinx.android.synthetic.main.item_job.view.item_job_number
import kotlinx.android.synthetic.main.item_job.view.item_job_state

private const val ITEM_HEIGHT = 88 // height in DP

/**
 * List adapter for jobs
 *
 * @author Dmytro Khmelenko
 */
class JobsListAdapter(private val context: Context, private val jobs: List<Job>?, private val listener: (Int) -> Unit) : RecyclerView.Adapter<JobsListAdapter.JobViewHolder>() {

    val itemHeight: Int
        get() = ITEM_HEIGHT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return JobViewHolder(v)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        if (jobs != null) {
            val job = jobs[position]

            // job data
            holder.itemView.item_job_number.text = context.getString(R.string.build_details_job_number, job.number)
            val state = job.state
            if (!TextUtils.isEmpty(state)) {
                val buildColor = BuildStateHelper.getBuildColor(state)
                holder.itemView.item_job_state.text = state
                holder.itemView.item_job_state.setTextColor(buildColor)
                holder.itemView.item_job_number.setTextColor(buildColor)

                val drawable = BuildStateHelper.getBuildImage(state)
                if (drawable != null) {
                    holder.itemView.item_job_number.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                }
            }

            // duration
            if (BuildStateHelper.isPassed(job.state)) {
                val started = DateTimeUtils.parseXmlDateTime(job.startedAt)
                val finished = DateTimeUtils.parseXmlDateTime(job.finishedAt)
                val durationInSeconds = (finished.time - started.time) / 1000L
                var duration = TimeConverter.durationToString(durationInSeconds)
                duration = context.getString(R.string.build_details_job_duration, duration)
                holder.itemView.item_job_duration.text = duration
            } else {
                val stateInProgress = context.getString(R.string.build_details_job_in_progress)
                val duration = context.getString(R.string.build_details_job_duration, stateInProgress)
                holder.itemView.item_job_duration.text = duration
            }
        }
    }

    override fun getItemCount(): Int {
        return jobs?.size ?: 0
    }

    /**
     * Viewholder class
     */
    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            ButterKnife.bind(this, itemView)
            itemView.isClickable = true
            itemView.card_view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            listener(layoutPosition)
        }
    }
}

