package com.khmelenko.lab.varis.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.adapter.JobsListAdapter;
import com.khmelenko.lab.varis.network.response.Job;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment with build jobs
 *
 * @author Dmytro Khmelenko
 */
public class JobsFragment extends Fragment {

    @BindView(R.id.jobs_recycler_view)
    RecyclerView mJobsRecyclerView;

    private JobsListAdapter mJobsListAdapter;
    private List<Job> mJobs = new ArrayList<>();

    private JobsListener mListener;

    /**
     * Creates new instance of the fragment
     *
     * @return Fragment instance
     */
    public static JobsFragment newInstance() {
        JobsFragment fragment = new JobsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public JobsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);
        ButterKnife.bind(this, view);

        mJobsRecyclerView.setNestedScrollingEnabled(false);
        mJobsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mJobsRecyclerView.setLayoutManager(layoutManager);

        mJobsListAdapter = new JobsListAdapter(getContext(), mJobs,
                position -> {
                    if (mJobs != null && !mJobs.isEmpty()) {
                        Job job = mJobs.get(position);
                        mListener.onJobSelected(job);
                    }
                });
        mJobsRecyclerView.setAdapter(mJobsListAdapter);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int itemHeight = (int) ((mJobsListAdapter.getItemHeight() * metrics.density) + 0.5);
        mJobsRecyclerView.getLayoutParams().height = itemHeight * mJobsListAdapter.getItemCount();

        return view;
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (JobsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement JobsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Sets the list of jobs
     *
     * @param jobs Jobs
     */
    public void setJobs(List<Job> jobs) {
        mJobs.clear();
        mJobs.addAll(jobs);

        if (mJobsListAdapter != null) {
            mJobsListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Interface for communication with this fragment
     */
    public interface JobsListener {

        /**
         * Handles job selection
         *
         * @param job Selected job
         */
        void onJobSelected(Job job);
    }

}
