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
import com.khmelenko.lab.travisclient.adapter.BranchesListAdapter;
import com.khmelenko.lab.travisclient.adapter.OnListItemListener;
import com.khmelenko.lab.travisclient.event.travis.BranchesLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.network.response.Branch;
import com.khmelenko.lab.travisclient.network.response.Branches;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.task.TaskManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Fragment with repository branches
 *
 * @author Dmytro Khmelenko
 */
public class BranchesFragment extends Fragment implements OnListItemListener {

    private static final String REPO_SLUG_KEY = "RepoSlug";

    @Bind(R.id.branches_swipe_view)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.branches_recycler_view)
    RecyclerView mBranchesRecyclerView;

    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    @Bind(R.id.empty_text)
    TextView mEmptyText;

    private BranchesListAdapter mBranchesListAdapter;
    private Branches mBranches;
    private String mRepoSlug;

    private BranchesListener mListener;

    /**
     * Creates new instance of the fragment
     *
     * @param repoSlug Repository slug
     * @return Fragment instance
     */
    public static BranchesFragment newInstance(String repoSlug) {
        BranchesFragment fragment = new BranchesFragment();
        Bundle args = new Bundle();
        args.putString(REPO_SLUG_KEY, repoSlug);
        fragment.setArguments(args);
        return fragment;
    }

    public BranchesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_branches, container, false);

        ButterKnife.bind(this, view);

        mBranchesRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBranchesRecyclerView.setLayoutManager(layoutManager);

        mBranchesListAdapter = new BranchesListAdapter(getContext(), mBranches, this);
        mBranchesRecyclerView.setAdapter(mBranchesListAdapter);

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
        taskManager.getBranches(mRepoSlug);
    }

    /**
     * Checks whether data existing or not
     */
    private void checkIfEmpty() {
        mEmptyText.setText(R.string.repo_details_branches_empty);
        if(mBranches == null || mBranches.getBranches().isEmpty()) {
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
    public void onEvent(BranchesLoadedEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);

        mBranches = event.getBranches();
        mBranchesListAdapter.setBranches(mBranches);
        mBranchesListAdapter.notifyDataSetChanged();

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

        String msg = getString(R.string.error_failed_loading_branches, event.getTaskError().getMessage());
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (BranchesListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BranchesListener");
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
            Branch branch = mBranches.getBranches().get(position);
            mListener.onBranchSelected(branch.getId());
        }
    }

    /**
     * Interface for communication with this fragment
     */
    public interface BranchesListener {

        /**
         * Handles selection of the branch
         *
         * @param buildId Id of the last build in branch
         */
        void onBranchSelected(long buildId);
    }

}
