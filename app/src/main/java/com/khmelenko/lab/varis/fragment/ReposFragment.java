package com.khmelenko.lab.varis.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.adapter.RepoListAdapter;
import com.khmelenko.lab.varis.network.response.Repo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment for main app screen
 *
 * @author Dmytro Khmelenko
 */
public class ReposFragment extends Fragment {

    private ReposFragmentListener mListener;

    @BindView(R.id.empty_text)
    TextView mEmptyView;

    @BindView(R.id.main_repos_swipe_view)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.main_repos_recycler_view)
    RecyclerView mReposRecyclerView;

    private RepoListAdapter mRepoListAdapter;
    private List<Repo> mRepos = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    /**
     * Creates new instance of the fragment
     *
     * @return Fragment instance
     */
    public static ReposFragment newInstance() {
        return new ReposFragment();
    }

    public ReposFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_repos, container, false);
        ButterKnife.bind(this, view);

        mReposRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mReposRecyclerView.setLayoutManager(layoutManager);

        mRepoListAdapter = new RepoListAdapter(mRepos, position -> {
            if (mListener != null) {
                mListener.onRepositorySelected(mRepos.get(position));
            }
        });
        mReposRecyclerView.setAdapter(mRepoListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_progress);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (mListener != null) {
                mListener.onRefreshData();
            }
        });

        return view;
    }

    /**
     * Checks whether data existing or not
     */
    private void checkIfEmpty() {
        mEmptyView.setText(R.string.repo_empty_text);
        if (mRepos.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Clears the fragment data
     */
    public void clearData() {
        mRepos.clear();
        mRepoListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (ReposFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Sets the list of repositories
     *
     * @param repos Repositories
     */
    public void setRepos(List<Repo> repos) {
        mRepos.clear();
        mRepos.addAll(repos);
        mRepoListAdapter.notifyDataSetChanged();

        checkIfEmpty();
    }

    /**
     * Sets the progress of the loading
     *
     * @param isLoading True, if loading is in progress. False otherwise
     */
    public void setLoadingProgress(boolean isLoading) {
        if (isLoading) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading_msg));
            }
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    /**
     * Handles the case when loading data failed
     */
    public void handleLoadingFailed(String message) {
        checkIfEmpty();

        String msg = getString(R.string.error_failed_loading_repos, message);
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Fragment listener
     */
    public interface ReposFragmentListener {

        /**
         * Handles repository selection
         *
         * @param repo Selected repository
         */
        void onRepositorySelected(Repo repo);

        /**
         * Handles request for refreshing data
         */
        void onRefreshData();
    }

}
