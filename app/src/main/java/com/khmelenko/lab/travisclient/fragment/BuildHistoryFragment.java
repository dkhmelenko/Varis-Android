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
import android.widget.TextView;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.BuildListAdapter;
import com.khmelenko.lab.travisclient.adapter.OnListItemListener;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.BuildHistoryLoadedEvent;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.BuildHistory;
import com.khmelenko.lab.travisclient.task.TaskManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Repository Build history
 *
 * @author Dmytro Khmelenko
 */
public class BuildHistoryFragment extends Fragment implements OnListItemListener {

    private static final String REPO_SLUG_KEY = "RepoSlug";

    @Bind(R.id.builds_history_swipe_view)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.builds_history_recycler_view)
    RecyclerView mBuildHistoryRecyclerView;

    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    @Bind(R.id.empty_text)
    TextView mEmptyText;

    private BuildListAdapter mBuildListAdapter;
    private BuildHistory mBuildHistory;
    private String mRepoSlug;

    private BuildHistoryListener mListener;

    /**
     * Creates new instance of the fragment
     *
     * @param repoSlug Repository slug
     * @return Fragment instance
     */
    public static BuildHistoryFragment newInstance(String repoSlug) {
        BuildHistoryFragment fragment = new BuildHistoryFragment();
        Bundle args = new Bundle();
        args.putString(REPO_SLUG_KEY, repoSlug);
        fragment.setArguments(args);
        return fragment;
    }

    public BuildHistoryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_build_history, container, false);
        ButterKnife.bind(this, view);

        mBuildHistoryRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBuildHistoryRecyclerView.setLayoutManager(layoutManager);

        mBuildListAdapter = new BuildListAdapter(getContext(), mBuildHistory, this);
        mBuildHistoryRecyclerView.setAdapter(mBuildListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_progress);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBuilds();
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);
        loadBuilds();

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
     * Starts loading build history
     */
    private void loadBuilds() {
        TaskManager taskManager = new TaskManager();
        taskManager.getBuildHistory(mRepoSlug);
    }

    /**
     * Checks whether data existing or not
     */
    private void checkIfEmpty() {
        mEmptyText.setText(R.string.repo_details_builds_empty);
        if(mBuildHistory == null || mBuildHistory.getBuilds().isEmpty()) {
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mEmptyText.setVisibility(View.GONE);
        }
    }

    /**
     * Raised on loaded repositories
     *
     * @param event Event data
     */
    public void onEvent(BuildHistoryLoadedEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);

        mBuildHistory = event.getBuildHistory();
        mBuildListAdapter.setBuildHistory(mBuildHistory);
        mBuildListAdapter.notifyDataSetChanged();

        checkIfEmpty();
    }

    /**
     * Raised on failed loading data
     *
     * @param event Event data
     */
    public void onEvent(LoadingFailedEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);

        checkIfEmpty();

        String msg = getString(R.string.error_failed_loading_build_history, event.getTaskError().getMessage());
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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
        if(mListener != null) {
            Build build = mBuildHistory.getBuilds().get(position);
            mListener.onBuildSelected(build.getId());
        }
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
    }

}
