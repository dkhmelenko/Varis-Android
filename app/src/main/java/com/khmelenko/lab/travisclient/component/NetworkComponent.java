package com.khmelenko.lab.travisclient.component;

import com.khmelenko.lab.travisclient.activity.AuthActivity;
import com.khmelenko.lab.travisclient.activity.BuildDetailsActivity;
import com.khmelenko.lab.travisclient.activity.MainActivity;
import com.khmelenko.lab.travisclient.activity.SearchResultsActivity;
import com.khmelenko.lab.travisclient.fragment.AuthFragment;
import com.khmelenko.lab.travisclient.fragment.BranchesFragment;
import com.khmelenko.lab.travisclient.fragment.BuildHistoryFragment;
import com.khmelenko.lab.travisclient.fragment.PullRequestsFragment;
import com.khmelenko.lab.travisclient.module.NetworkModule;
import com.khmelenko.lab.travisclient.module.NotificationModule;
import com.khmelenko.lab.travisclient.task.TaskHelper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Network component
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Singleton
@Component(modules = {NetworkModule.class, NotificationModule.class})
public interface NetworkComponent {

    void inject(MainActivity activity);
    void inject(AuthActivity activity);
    void inject(BuildDetailsActivity activity);
    void inject(SearchResultsActivity activity);

    void inject(AuthFragment fragment);
    void inject(BranchesFragment fragment);
    void inject(BuildHistoryFragment fragment);
    void inject(PullRequestsFragment fragment);

    void inject(TaskHelper task);
}
