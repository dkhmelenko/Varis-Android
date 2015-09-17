package com.khmelenko.lab.travisclient.fragment;

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
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.PullRequestsListAdapter;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.RequestsLoadedEvent;
import com.khmelenko.lab.travisclient.network.response.Requests;
import com.khmelenko.lab.travisclient.task.TaskManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Repository pull requests
 *
 * @author Dmytro Khmelenko
 */
public class PullRequestsFragment extends Fragment {

    private static final String REPO_SLUG_KEY = "RepoSlug";

    @Bind(R.id.pull_requests_swipe_view)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.pull_requests_recycler_view)
    RecyclerView mPullRequestsRecyclerView;

    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    private PullRequestsListAdapter mPullRequestsListAdapter;
    private Requests mRequests;
    private String mRepoSlug;

    private PullRequestsListener mListener;

    /**
     * Creates new instance of the fragment
     *
     * @param repoSlug Repository slug
     * @return Fragment instance
     */
    public static PullRequestsFragment newInstance(String repoSlug) {
        PullRequestsFragment fragment = new PullRequestsFragment();
        Bundle args = new Bundle();
        args.putString(REPO_SLUG_KEY, repoSlug);
        fragment.setArguments(args);
        return fragment;
    }

    public PullRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRepoSlug = getArguments().getString(REPO_SLUG_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pull_requests, container, false);

        ButterKnife.bind(this, view);

        mPullRequestsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mPullRequestsRecyclerView.setLayoutManager(layoutManager);

        mPullRequestsListAdapter = new PullRequestsListAdapter(getContext(), mRequests);
        mPullRequestsRecyclerView.setAdapter(mPullRequestsListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_progress);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBranches();
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);
        loadBranches();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Starts loading branches
     */
    private void loadBranches() {
        TaskManager taskManager = new TaskManager();
        taskManager.getRequests(mRepoSlug);
    }

    /**
     * Raised on loaded requests
     *
     * @param event Event data
     */
    public void onEvent(RequestsLoadedEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);

        mRequests = event.getRequests();
        mPullRequestsListAdapter.setRequests(mRequests);
        mPullRequestsListAdapter.notifyDataSetChanged();
    }

    /**
     * Raised on failed loading data
     *
     * @param event Event data
     */
    public void onEvent(LoadingFailedEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);

        String msg = getString(R.string.error_failed_loading_build_history, event.getTaskError().getMessage());
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PullRequestsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PullRequestsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interface for communication with this fragment
     */
    public interface PullRequestsListener {

        /**
         * Handles selection of the pull request
         *
         * @param pullRequest Pull request name
         */
        void onPullRequestSelected(String pullRequest);
    }
}
