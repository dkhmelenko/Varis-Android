package com.khmelenko.lab.travisclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.BranchesListAdapter;
import com.khmelenko.lab.travisclient.adapter.JobsListAdapter;
import com.khmelenko.lab.travisclient.network.response.Job;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment with build jobs
 *
 * @author Dmytro Khmelenko
 */
public class JobsFragment extends Fragment {

    @Bind(R.id.jobs_recycler_view)
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

        mJobsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mJobsRecyclerView.setLayoutManager(layoutManager);

        mJobsListAdapter = new JobsListAdapter(getContext(), mJobs);
        mJobsRecyclerView.setAdapter(mJobsListAdapter);

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
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

        mJobsListAdapter.notifyDataSetChanged();
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
        void onJobSelected(String job);
    }

}
