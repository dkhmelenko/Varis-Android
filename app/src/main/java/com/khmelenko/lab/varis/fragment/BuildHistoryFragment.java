package com.khmelenko.lab.varis.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.adapter.BuildListAdapter;
import com.khmelenko.lab.varis.adapter.OnListItemListener;
import com.khmelenko.lab.varis.network.response.Build;
import com.khmelenko.lab.varis.network.response.BuildHistory;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Repository Build history
 *
 * @author Dmytro Khmelenko
 */
public class BuildHistoryFragment extends Fragment implements OnListItemListener {

    @Bind(R.id.list_refreshable_swipe_view)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.list_refreshable_recycler_view)
    RecyclerView mBuildHistoryRecyclerView;

    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    @Bind(R.id.empty_text)
    TextView mEmptyText;

    private BuildListAdapter mBuildListAdapter;
    private BuildHistory mBuildHistory;

    private BuildHistoryListener mListener;

    /**
     * Creates new instance of the fragment
     *
     * @return Fragment instance
     */
    public static BuildHistoryFragment newInstance() {
        BuildHistoryFragment fragment = new BuildHistoryFragment();
        return fragment;
    }

    public BuildHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_refreshable, container, false);
        ButterKnife.bind(this, view);

        mBuildHistoryRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBuildHistoryRecyclerView.setLayoutManager(layoutManager);

        mBuildListAdapter = new BuildListAdapter(mBuildHistory, this);
        mBuildHistoryRecyclerView.setAdapter(mBuildListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_progress);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListener.onReloadBuildHistory();
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);

        return view;
    }


    /**
     * Checks whether data existing or not
     */
    private void checkIfEmpty() {
        mEmptyText.setText(R.string.repo_details_builds_empty);
        if (mBuildHistory == null || mBuildHistory.getBuilds().isEmpty()) {
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mEmptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (BuildHistoryListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BuildHistoryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(int position) {
        if (mListener != null) {
            Build build = mBuildHistory.getBuilds().get(position);
            mListener.onBuildSelected(build.getId());
        }
    }

    /**
     * Sets build history
     *
     * @param buildHistory Build history
     */
    public void setBuildHistory(BuildHistory buildHistory) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);

        if (buildHistory != null) {
            mBuildHistory = buildHistory;
            mBuildListAdapter.setBuildHistory(mBuildHistory);
            mBuildListAdapter.notifyDataSetChanged();
        }

        checkIfEmpty();
    }

    /**
     * Interface for communication with this fragment
     */
    public interface BuildHistoryListener {

        /**
         * Handles build selection
         *
         * @param buildNumber Build number
         */
        void onBuildSelected(long buildNumber);

        /**
         * Handles reload data request
         */
        void onReloadBuildHistory();
    }

}
