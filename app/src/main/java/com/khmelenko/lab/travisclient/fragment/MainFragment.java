package com.khmelenko.lab.travisclient.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.OnListItemListener;
import com.khmelenko.lab.travisclient.adapter.RepoListAdapter;
import com.khmelenko.lab.travisclient.network.response.Repo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Fragment for main app screen
 *
 * @author Dmytro Khmelenko
 */
public class MainFragment extends Fragment {

    private MainFragmentListener mListener;

    @Bind(R.id.empty_text)
    TextView mEmptyView;

    @Bind(R.id.main_repos_swipe_view)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.main_repos_recycler_view)
    RecyclerView mReposRecyclerView;

    private RepoListAdapter mRepoListAdapter;
    private List<Repo> mRepos = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    /**
     * Creates new instance of the fragment
     *
     * @return Fragment instance
     */
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        mReposRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mReposRecyclerView.setLayoutManager(layoutManager);

        mRepoListAdapter = new RepoListAdapter(getActivity(), mRepos, new OnListItemListener() {
            @Override
            public void onItemSelected(int position) {
                if (mListener != null) {
                    mListener.onRepositorySelected(mRepos.get(position));
                }
            }
        });
        mReposRecyclerView.setAdapter(mRepoListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_progress);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mListener != null) {
                    mListener.onRefreshData();
                }
            }
        });

        mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading_msg));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mListener != null) {
            mListener.onRefreshData();
        }
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MainFragmentListener) activity;
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
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading_msg));
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressDialog.dismiss();
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
    public interface MainFragmentListener {

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
