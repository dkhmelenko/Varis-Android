package com.khmelenko.lab.varis.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.converter.BuildStateHelper;
import com.khmelenko.lab.varis.converter.TimeConverter;
import com.khmelenko.lab.varis.network.response.Job;
import com.khmelenko.lab.varis.util.DateTimeUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * List adapter for jobs
 *
 * @author Dmytro Khmelenko
 */
public final class JobsListAdapter extends RecyclerView.Adapter<JobsListAdapter.JobViewHolder> {

    private final Context mContext;
    private List<Job> mJobs;
    private final OnListItemListener mListener;

    public JobsListAdapter(Context context, List<Job> jobs, OnListItemListener listener) {
        mContext = context;
        mJobs = jobs;
        mListener = listener;
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(v);
    }

    @Override
    public void onBindViewHolder(JobViewHolder holder, int position) {
        if (mJobs != null) {
            Job job = mJobs.get(position);

            // job data
            holder.mNumber.setText(mContext.getString(R.string.build_details_job_number, job.getNumber()));
            String state = job.getState();
            if (!TextUtils.isEmpty(state)) {
                int buildColor = BuildStateHelper.getBuildColor(state);
                holder.mState.setText(state);
                holder.mState.setTextColor(buildColor);
                holder.mNumber.setTextColor(buildColor);

                Drawable drawable = BuildStateHelper.getBuildImage(state);
                if (drawable != null) {
                    holder.mNumber.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                }
            }

            // duration
            if (BuildStateHelper.isPassed(job.getState())) {
                Date started = DateTimeUtils.parseXmlDateTime(job.getStartedAt());
                Date finished = DateTimeUtils.parseXmlDateTime(job.getFinishedAt());
                long durationInSeconds = (finished.getTime() - started.getTime()) / 1000L;
                String duration = TimeConverter.durationToString(durationInSeconds);
                duration = mContext.getString(R.string.build_details_job_duration, duration);
                holder.mDuration.setText(duration);
            } else {
                String stateInProgress = mContext.getString(R.string.build_details_job_in_progress);
                String duration = mContext.getString(R.string.build_details_job_duration, stateInProgress);
                holder.mDuration.setText(duration);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mJobs != null ? mJobs.size() : 0;
    }

    public int getItemHeight() {
        return JobViewHolder.HEIGHT;
    }

    /**
     * Viewholder class
     */
    class JobViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final int HEIGHT = 88; // height in DP

        @BindView(R.id.card_view)
        View mParent;

        @BindView(R.id.item_job_number)
        TextView mNumber;

        @BindView(R.id.item_job_state)
        TextView mState;

        @BindView(R.id.item_job_duration)
        TextView mDuration;

        public JobViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            mParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemSelected(getLayoutPosition());
            }
        }
    }
}

