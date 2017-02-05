package com.khmelenko.lab.travisclient.fragment;

import android.content.Context;
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

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.OnListItemListener;
import com.khmelenko.lab.travisclient.adapter.PullRequestsListAdapter;
import com.khmelenko.lab.travisclient.network.response.RequestData;
import com.khmelenko.lab.travisclient.network.response.Requests;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Repository pull requests
 *
 * @author Dmytro Khmelenko
 */
public class PullRequestsFragment extends Fragment implements OnListItemListener {

    @Bind(R.id.pull_requests_swipe_view)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.pull_requests_recycler_view)
    RecyclerView mPullRequestsRecyclerView;

    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    @Bind(R.id.empty_text)
    TextView mEmptyText;

    private PullRequestsListAdapter mPullRequestsListAdapter;
    private Requests mRequests;
    private List<RequestData> mPullRequests;

    private PullRequestsListener mListener;

    public PullRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Creates new instance of the fragment
     *
     * @return Fragment instance
     */
    public static PullRequestsFragment newInstance() {
        return new PullRequestsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pull_requests, container, false);
        ButterKnife.bind(this, view);

        mPullRequestsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mPullRequestsRecyclerView.setLayoutManager(layoutManager);

        mPullRequestsListAdapter = new PullRequestsListAdapter(mRequests, this);
        mPullRequestsRecyclerView.setAdapter(mPullRequestsListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_progress);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListener.onReloadPullRequests();
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);

        return view;
    }

    /**
     * Fetches pull requests
     */
    private List<RequestData> fetchPullRequests(Requests requests) {
        List<RequestData> pullRequest = new ArrayList<>();
        if (mRequests != null) {
            for (RequestData request : requests.getRequests()) {
                if (request.isPullRequest() && !pullRequest.contains(request)) {
                    pullRequest.add(request);
                }
            }
        }
        return pullRequest;
    }

    /**
     * Checks whether data existing or not
     */
    private void checkIfEmpty() {
        mEmptyText.setText(R.string.repo_details_pull_request_empty);
        if (mPullRequests == null || mPullRequests.isEmpty()) {
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mEmptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (PullRequestsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement PullRequestsListener");
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
            RequestData requestData = mPullRequests.get(position);
            mListener.onPullRequestSelected(requestData.getBuildId());
        }
    }

    /**
     * Sets pull request data
     *
     * @param request Pull requests
     */
    public void setPullRequests(Requests request) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);

        if (request != null) {
            mRequests = request;
            mPullRequests = fetchPullRequests(mRequests);
            mPullRequestsListAdapter.setRequests(mRequests, mPullRequests);
            mPullRequestsListAdapter.notifyDataSetChanged();
        }

        checkIfEmpty();
    }

    /**
     * Interface for communication with this fragment
     */
    public interface PullRequestsListener {

        /**
         * Handles selection of the pull request
         *
         * @param buildId ID of the build of the pull request
         */
        void onPullRequestSelected(long buildId);

        /**
         * Handles reload action for pull request
         */
        void onReloadPullRequests();
    }
}
