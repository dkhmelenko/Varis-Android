package com.khmelenko.lab.varis.repodetails;

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

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.adapter.OnListItemListener;
import com.khmelenko.lab.varis.network.response.Branch;
import com.khmelenko.lab.varis.network.response.Branches;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment with repository branches
 *
 * @author Dmytro Khmelenko
 */
public class BranchesFragment extends Fragment implements OnListItemListener {

    @BindView(R.id.list_refreshable_swipe_view)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.list_refreshable_recycler_view)
    RecyclerView mBranchesRecyclerView;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.empty_text)
    TextView mEmptyText;

    private BranchesListAdapter mBranchesListAdapter;
    private Branches mBranches;

    private BranchesListener mListener;

    /**
     * Creates new instance of the fragment
     *
     * @return Fragment instance
     */
    public static BranchesFragment newInstance() {
        return new BranchesFragment();
    }

    public BranchesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_refreshable, container, false);
        ButterKnife.bind(this, view);

        mBranchesRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBranchesRecyclerView.setLayoutManager(layoutManager);

        mBranchesListAdapter = new BranchesListAdapter(mBranches, this);
        mBranchesRecyclerView.setAdapter(mBranchesListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_progress);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mListener.onReloadBranches());

        mProgressBar.setVisibility(View.VISIBLE);

        return view;
    }

    /**
     * Checks whether data existing or not
     */
    private void checkIfEmpty() {
        mEmptyText.setText(R.string.repo_details_branches_empty);
        if (mBranches == null || mBranches.getBranches().isEmpty()) {
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mEmptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context activity) {
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
        if (mListener != null) {
            Branch branch = mBranches.getBranches().get(position);
            mListener.onBranchSelected(branch.getId());
        }
    }

    /**
     * Sets branches data
     *
     * @param branches Branches
     */
    public void setBranches(Branches branches) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);

        if (branches != null) {
            mBranches = branches;
            mBranchesListAdapter.setBranches(mBranches);
            mBranchesListAdapter.notifyDataSetChanged();
        }

        checkIfEmpty();
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

        /**
         * Handles request for reloading branches data
         */
        void onReloadBranches();
    }

}
