package com.khmelenko.lab.travisclient.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.converter.BuildStateHelper;
import com.khmelenko.lab.travisclient.converter.TimeConverter;
import com.khmelenko.lab.travisclient.network.response.Job;

import java.util.List;

/**
 * List adapter for jobs
 *
 * @author Dmytro Khmelenko
 */
public class JobsListAdapter extends RecyclerView.Adapter<JobsListAdapter.JobViewHolder> {

    private final Context mContext;
    private List<Job> mJobs;

    public JobsListAdapter(Context context, List<Job> jobs) {
        mContext = context;
        mJobs = jobs;
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
                    drawable.setBounds(0, 0, 30, 30);
                    holder.mNumber.setCompoundDrawables(drawable, null, null, null);
                }
            }

            // duration
            if (BuildStateHelper.isPassed(job.getState())) {
                String duration = TimeConverter.durationToString(job.getDuration());
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

    /**
     * Viewholder class
     */
    class JobViewHolder extends RecyclerView.ViewHolder {

        View mParent;
        TextView mNumber;
        TextView mState;
        TextView mDuration;

        public JobViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mParent = itemView.findViewById(R.id.card_view);
            mNumber = (TextView) itemView.findViewById(R.id.item_job_number);
            mState = (TextView) itemView.findViewById(R.id.item_job_state);
            mDuration = (TextView) itemView.findViewById(R.id.item_job_duration);
        }
    }
}

