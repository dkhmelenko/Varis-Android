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
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.util.Converter;
import com.khmelenko.lab.travisclient.util.DateTimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter class for the list of repositories
 *
 * @author Dmytro Khmelenko
 */
public class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.RepoViewHolder> {

    private final List<Repo> mRepos;
    private final Context mContext;

    public RepoListAdapter(Context context, List<Repo> repos) {
        mContext = context;
        mRepos = repos;
    }

    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_repo, viewGroup, false);
        return new RepoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RepoViewHolder repoViewHolder, int i) {
        Repo repo = mRepos.get(i);
        repoViewHolder.mName.setText(repo.getSlug());
        String state = repo.getLastBuildState();
        if (!TextUtils.isEmpty(state)) {
            int passedColor = mContext.getResources().getColor(R.color.build_state_passed);
            int failedColor = mContext.getResources().getColor(R.color.build_state_failed);
            int startedColor = mContext.getResources().getColor(R.color.build_state_started);
            switch (state)
            {
                case "started":
                    repoViewHolder.mName.setTextColor(startedColor);
                    break;
                case "passed":
                    repoViewHolder.mName.setTextColor(passedColor);
                    Drawable passIcon = mContext.getResources().getDrawable(R.drawable.build_passed);
                    passIcon.setBounds(0,0,30,30);
                    repoViewHolder.mName.setCompoundDrawables(passIcon, null, null, null);
                    break;
                case "failed":
                case "errored":
                    repoViewHolder.mName.setTextColor(failedColor);
                    Drawable failIcon = mContext.getResources().getDrawable(R.drawable.build_failed);
                    failIcon.setBounds(0,0,30,30);
                    repoViewHolder.mName.setCompoundDrawables(failIcon, null, null, null);
                    break;
            }
        }

        // finished at
        if (!TextUtils.isEmpty(repo.getLastBuildFinishedAt())) {
            try {
                Date finishedAt = DateTimeUtils.parseXmlDateTime(repo.getLastBuildFinishedAt());
                String formattedDate = DateTimeUtils.formatDateTimeLocal(finishedAt);
                formattedDate = mContext.getString(R.string.repo_finished_at, formattedDate);
                repoViewHolder.mFinished.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            String stateInProgress = mContext.getString(R.string.repo_build_in_progress);
            String finished = mContext.getString(R.string.repo_finished_at, stateInProgress);
            repoViewHolder.mFinished.setText(finished);
        }

        // duration
        if(repo.getLastBuildDuration() != 0) {
            String duration = Converter.durationToString(repo.getLastBuildDuration());
            duration = mContext.getString(R.string.repo_duration, duration);
            repoViewHolder.mDuration.setText(duration);
        } else {
            String stateInProgress = mContext.getString(R.string.repo_build_in_progress);
            String duration = mContext.getString(R.string.repo_duration, stateInProgress);
            repoViewHolder.mDuration.setText(duration);
        }
    }

    @Override
    public int getItemCount() {
        return mRepos.size();
    }

    /**
     * Viewholder class
     */
    class RepoViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mDuration;
        private TextView mFinished;

        public RepoViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            mName = (TextView) itemView.findViewById(R.id.item_repo_name);
            mDuration = (TextView) itemView.findViewById(R.id.item_repo_duration);
            mFinished = (TextView) itemView.findViewById(R.id.item_repo_finished);
        }
    }
}
